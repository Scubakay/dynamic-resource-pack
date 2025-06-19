@echo off
REM Simulate an in-place overwrite (like IntelliJ) by toggling resource-pack and resource-pack-sha1 lines directly

echo Toggling resource-pack and resource-pack-sha1 lines in server.properties...

REM Write to a temp file, then atomically replace server.properties in one action
powershell -NoProfile -Command ^
    "$lines = Get-Content 'run/server.properties'; " ^
    "$out = foreach ($line in $lines) { " ^
    "    if ($line -match '^resource-pack=(.+)$') { '# resource-pack=' + $Matches[1] } " ^
    "    elseif ($line -match '^# resource-pack=(.+)$') { 'resource-pack=' + $Matches[1] } " ^
    "    elseif ($line -match '^resource-pack-sha1=(.+)$') { '# resource-pack-sha1=' + $Matches[1] } " ^
    "    elseif ($line -match '^# resource-pack-sha1=(.+)$') { 'resource-pack-sha1=' + $Matches[1] } " ^
    "    else { $line } " ^
    "}; " ^
    "$tmp = [System.IO.Path]::GetTempFileName(); $out | Set-Content $tmp; " ^
    "Move-Item -Force $tmp 'run/server.properties'"

echo Done.
