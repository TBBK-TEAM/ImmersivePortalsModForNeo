# Generates the per-variant command stick item models from the single source of truth,
# CommandStickModelMapping.ENTRIES, and validates the result.
#
#   * every entry's command must match a variant actually registered by CommandStickItem
#   * every referenced texture must exist inside the vanilla client jar
#   * CommandStickItem must not register a variant that the mapping table misses
#
# Usage:  powershell -NoProfile -ExecutionPolicy Bypass -File misc/generate_command_stick_models.ps1
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem

$repo = Split-Path -Parent $PSScriptRoot
$mappingJava = Join-Path $repo 'src/main/java/qouteall/imm_ptl/peripheral/CommandStickModelMapping.java'
$stickJava   = Join-Path $repo 'src/main/java/qouteall/imm_ptl/peripheral/CommandStickItem.java'
$modelDir    = Join-Path $repo 'src/main/resources/assets/immersive_portals/models/item'
$clientJar   = Join-Path $env:USERPROFILE '.gradle/caches/neoformruntime/artifacts/minecraft_1.21.1_client.jar'

if (-not (Test-Path $clientJar)) { throw "vanilla client jar not found: $clientJar" }

# ---------------------------------------------------------------- 1. read the mapping table
$mappingText = Get-Content $mappingJava -Raw
$entries = @()
foreach ($m in [regex]::Matches($mappingText, 'new Entry\(\s*"([^"]+)"\s*,\s*"([^"]+)"\s*\)')) {
    $entries += [pscustomobject]@{ Command = $m.Groups[1].Value; Texture = $m.Groups[2].Value }
}
if ($entries.Count -eq 0) { throw 'no entries parsed from CommandStickModelMapping.java' }
Write-Host ("mapping entries: {0}" -f $entries.Count)

# ---------------------------------------------------------------- 2. registered variants
$stickText = Get-Content $stickJava -Raw
$start = $stickText.IndexOf('public static void registerCommandStickTypes()')
$end   = $stickText.IndexOf('private static Data registerPortalSubCommandStick(String name)')
$body  = $stickText.Substring($start, $end - $start)

$registered = @()
$pattern = 'registerPortalSubCommandStick\(\s*"([^"]+)"(?:\s*,\s*"([^"]+)")?\s*\)|registerBuiltInCommandStick\(\s*new Data\(\s*"([^"]+)"'
foreach ($m in [regex]::Matches($body, $pattern)) {
    if ($m.Groups[3].Success) {
        $registered += $m.Groups[3].Value
    } elseif ($m.Groups[2].Success) {
        $registered += ('/portal ' + $m.Groups[2].Value)
    } else {
        $registered += ('/portal ' + $m.Groups[1].Value)
    }
}
Write-Host ("registered variants: {0}" -f $registered.Count)

$problems = 0
foreach ($e in $entries) {
    if ($registered -notcontains $e.Command) {
        Write-Host ("  ERROR mapping command is not a registered variant: {0}" -f $e.Command)
        $problems++
    }
}
foreach ($r in $registered) {
    if (($entries | ForEach-Object { $_.Command }) -notcontains $r) {
        Write-Host ("  ERROR registered variant has no icon: {0}" -f $r)
        $problems++
    }
}
$dupes = $entries | Group-Object Command | Where-Object { $_.Count -gt 1 }
foreach ($d in $dupes) { Write-Host ("  ERROR duplicate mapping for: {0}" -f $d.Name); $problems++ }
if ($problems -gt 0) { throw ("mapping table is inconsistent ({0} problem(s))" -f $problems) }
Write-Host '  ok: mapping table covers exactly the registered variants'

# ---------------------------------------------------------------- 3. texture existence
$z = [System.IO.Compression.ZipFile]::OpenRead($clientJar)
$tex = @{}
foreach ($e in $z.Entries) {
    if ($e.FullName -like 'assets/minecraft/textures/*/*.png') {
        $tex[($e.FullName -replace '^assets/minecraft/textures/', '' -replace '\.png$', '')] = $true
    }
}
$z.Dispose()
Write-Host ("vanilla textures indexed: {0}" -f $tex.Count)

foreach ($e in $entries) {
    $parts = $e.Texture -split ':', 2
    if ($parts[0] -eq 'minecraft') {
        if (-not $tex.ContainsKey($parts[1])) {
            Write-Host ("  ERROR texture does not exist in vanilla: {0}" -f $e.Texture)
            $problems++
        }
    } else {
        $local = Join-Path $repo ("src/main/resources/assets/{0}/textures/{1}.png" -f $parts[0], $parts[1])
        if (-not (Test-Path $local)) {
            Write-Host ("  ERROR mod texture not found: {0} -> {1}" -f $e.Texture, $local)
            $problems++
        }
    }
}
if ($problems -gt 0) { throw ("texture validation failed ({0} problem(s))" -f $problems) }
Write-Host '  ok: every referenced texture exists'

# ---------------------------------------------------------------- 4. write model json files
Get-ChildItem $modelDir -Filter 'command_stick_*.json' -ErrorAction SilentlyContinue | Remove-Item -Force

$utf8 = New-Object System.Text.UTF8Encoding($false)
$overrides = @()
for ($i = 0; $i -lt $entries.Count; $i++) {
    $n = $i + 1
    $name = 'command_stick_{0:d2}' -f $n
    $json = "{`r`n  `"parent`": `"minecraft:item/handheld`",`r`n  `"textures`": {`r`n    `"layer0`": `"$($entries[$i].Texture)`"`r`n  }`r`n}`r`n"
    [System.IO.File]::WriteAllText((Join-Path $modelDir "$name.json"), $json, $utf8)
    $overrides += "    { `"predicate`": { `"immersive_portals:command_stick_model`": $n }, `"model`": `"immersive_portals:item/$name`" }"
}

$base = "{`r`n  `"parent`": `"minecraft:item/handheld`",`r`n  `"textures`": {`r`n    `"layer0`": `"immersive_portals:item/command_stick`"`r`n  },`r`n  `"overrides`": [`r`n" +
        ($overrides -join ",`r`n") + "`r`n  ]`r`n}`r`n"
[System.IO.File]::WriteAllText((Join-Path $modelDir 'command_stick.json'), $base, $utf8)

Write-Host ("wrote command_stick.json + {0} variant models" -f $entries.Count)
