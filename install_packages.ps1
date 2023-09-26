# include project helper functions
. "${PWD}\functions.ps1"

printSectionBanner "Upload Packages"
printSectionLine " "
printSectionLine " "

printSectionStart "Upload packages to Author"
./package.ps1 -LOCAL -LOCAL_AEM_HOST_URL "https://author.localhost"
printSectionEnd "Upload packages to Author"
printSectionLine " "
printSectionLine " "
printSectionStart "Upload packages to Publish"
./package.ps1 -LOCAL -LOCAL_AEM_HOST_URL "https://publish.localhost"
printSectionEnd "Upload packages to Publish"