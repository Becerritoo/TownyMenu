from pathlib import Path
import subprocess
r=Path(__file__).resolve().parent
m=Path('/home/becerritoo/.m2/repository')
deps=['org/mockito/mockito-core/4.11.0/mockito-core-4.11.0.jar','net/bytebuddy/byte-buddy/1.12.19/byte-buddy-1.12.19.jar','net/bytebuddy/byte-buddy-agent/1.12.19/byte-buddy-agent-1.12.19.jar','org/objenesis/objenesis/3.3/objenesis-3.3.jar']
cp=(r/'build-permissions/classpath.txt').read_text()+':'+':'.join(str(m/p) for p in deps)
target=r/'build-permissions/test-classes'
subprocess.run(['javac','-proc:none','-cp',cp,'-d',str(target),str(r/'src/test/java/me/cobrex/townymenu/utils/PlotAccessTest.java')],check=True)
result = subprocess.run(['java','-Dnet.bytebuddy.experimental=true','-cp',str(target)+':'+cp,'me.cobrex.townymenu.utils.PlotAccessTest'])
raise SystemExit(result.returncode)
