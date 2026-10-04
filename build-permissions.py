from pathlib import Path
import subprocess, zipfile
r = Path(__file__).resolve().parent
server = Path('/srv/minecraft/server-jl')
base = Path('/srv/minecraft/dev/mc-build/releases/TownyMenu/2.0.7-fix.1/TownyMenu-2.0.7-fix.1.jar')
classes = r/'build-permissions/classes'
classes.mkdir(parents=True, exist_ok=True)
cp = ':'.join(map(str, [base, server/'plugins/Towny-0.102.0.0.jar', *sorted((server/'libraries').rglob('*.jar'))]))
files = ['utils/PlotAccess.java', 'commands/PlotMenuCommand.java', 'plot/PlotMenu.java', 'plot/PlotPermMenu.java', 'plot/PlotToggleSettingsMenu.java']
subprocess.run(['javac', '--release', '21', '-proc:none', '-cp', cp, '-d', str(classes), *[str(r/'src/main/java/me/cobrex/townymenu'/f) for f in files]], check=True)
(r/'build-permissions/classpath.txt').write_text(str(classes)+':'+cp)
jar = r/'build-permissions/TownyMenu-2.0.7-fix.2.jar'
with zipfile.ZipFile(base) as old, zipfile.ZipFile(jar, 'w', zipfile.ZIP_DEFLATED) as new:
    for item in old.infolist():
        if (classes/item.filename).is_file(): continue
        data = old.read(item)
        if item.filename == 'plugin.yml': data = data.replace(b'2.0.7-fix.1', b'2.0.7-fix.2')
        new.writestr(item, data)
    for p in classes.rglob('*.class'): new.write(p, p.relative_to(classes))
print(jar)
