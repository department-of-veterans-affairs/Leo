# A Shell script to help with the conversion from Flap to Leo
find . -name '*.java' -print | xargs perl -p -i -e 's/gov.va.vinci.flap/gov.va.vinci.leo/g'
find . -name '*.java' -print | xargs perl -p -i -e 's/gov.va.vinci.marian/gov.va.vinci.leo/g'
find . -name '*.java' -print | xargs perl -p -i -e 's/FlapAEDescriptor/LeoAEDescriptor/g'
find . -name '*.java' -print | xargs perl -p -i -e 's/FlapDeployDescriptor/LeoDeployDescriptor/g'
find . -name '*.java' -print | xargs perl -p -i -e 's/FlapTypeSystemDescription/LeoTypeSystemDescription/g'
find . -name '*.java' -print | xargs perl -p -i -e 's/MarianBaseAnnotator/LeoBaseAnnotator/g'
