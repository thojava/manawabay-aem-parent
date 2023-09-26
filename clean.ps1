Param(
    [string]$GIT_CLEAN_CHECK = "git clean -xn services; git clean -xn logs",
    [string]$GIT_CLEAN = "git clean -xdf services; git clean -xdf logs",
    [string]$CURRENT_PATH = "${PWD}",
    [string]$OS = ( $IsWindows ? "win32" : ( $IsMacOS ? "darwin" : "linux" ) ),
    [switch]$CLEAN = $false,
    [switch]$DEBUG = $false
)

. "${PWD}\functions.ps1"

Function PrintInfo
{
    printSectionBanner "Reset Config"

    printSectionLine "GIT_CLEAN_CHECK: ${GIT_CLEAN_CHECK}"
    printSectionLine "GIT_CLEAN: ${GIT_CLEAN}"
    printSectionLine "CURRENT_PATH: ${CURRENT_PATH}"

}

Function StartCheck
{

    printSectionBanner "Reset Check"

    try {
        Invoke-Expression -Command "${GIT_CLEAN_CHECK}"
    } finally {
        Set-Location -Path "${CURRENT_PATH}"
    }

}

Function StartReset
{

    printSectionBanner "Reset"

    try {
        Invoke-Expression -Command "${GIT_CLEAN}"
    } finally {
        Set-Location -Path "${CURRENT_PATH}"
    }



}

PrintInfo

if ( $CLEAN ) {
    StartReset
} else {
    StartCheck
}
