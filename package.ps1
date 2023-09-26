Param(
    [string]$LOCAL_TOKEN = (Get-Content "local-token.txt"),
    [string]$LOCAL_AEM_HOST_HEADER = "-H ""Authorization: Basic ${LOCAL_TOKEN}""",
    [string]$LOCAL_AEM_HOST_URL = "https://author.localhost",
    [string]$DESTINATION_AEM_TOKEN = (Get-Content "dev-token.txt" | ConvertFrom-Json).accessToken,
    [string]$DESTINATION_AEM_HOST_URL = "https://author-p63354-e722664.adobeaemcloud.com",
    [string]$DESTINATION_AEM_HOST_HEADER = "-H ""Authorization: Bearer ${DESTINATION_AEM_TOKEN}""",
    [string]$DESTINATION_AEM_HOST_TEST_URL = "https://publish-p63354-e722664.adobeaemcloud.com",
    [string]$SOURCE_PATH_BASE = "/etc/packages/my_packages",
    [string]$SOURCE_PATH_LIST = "./package-install.txt",
    [string]$BATCH_SIZE = 4,
    [string]$BATCH_WAIT_SECONDS = 300,
    [string]$PACKAGE_PREFIX = "dev",
    [switch]$REPLICATE_ALWAYS = $False,
    [switch]$SKIP_REPLICATION_CHECK = $True,
    [switch]$DELETE_ALWAYS = $False,
    # ensure package paths are same as what we want
    [switch]$PACKAGE_VALIDATE = $False,
    #rebuild packages before downloading
    [switch]$PACKAGE_REBUILD = $False,
    # deploy local packages to a new host
    [switch]$PACKAGE_DEPLOY = $False,
    [switch]$DOLOG = $False,
    [switch]$LOCAL = $True
)

# include project helper functions
. "${PWD}\functions.ps1"

# get package list from file $SOURCE_PATH_LIST
$PACKAGE_LIST = Get-Content $SOURCE_PATH_LIST

# for each $PACKAGE_LIST
foreach ($PACKAGE in $PACKAGE_LIST) {
    printSubSectionStart "Processing package $PACKAGE"
    $PACKAGE_FILE = $PACKAGE

    # check if file $PACKAGE_FILE exists
    if (-not(Test-Path $PACKAGE_FILE)) {
        printSectionLine  "Package file $PACKAGE_FILE does not exist"
        continue
    }

    $PAKAGE_NAME = Split-Path $PACKAGE -Leaf
    $SOURCE_HOST = $LOCAL_AEM_HOST_URL
    $HEADER_AUTH_TOKEN = $LOCAL_AEM_HOST_HEADER


    if (-not($LOCAL)) {
        $SOURCE_HOST = $DESTINATION_AEM_HOST_URL
        $HEADER_AUTH_TOKEN = $DESTINATION_AEM_TOKEN
    }
    printSectionLine  "LOCAL: $LOCAL"
    printSectionLine  "PACKAGE_FILE: $PACKAGE_FILE"
    printSectionLine  "PAKAGE_NAME: $PAKAGE_NAME"
    printSectionLine  "SOURCE_HOST: $SOURCE_HOST"
    printSectionLine  "HEADER_AUTH_TOKEN: $HEADER_AUTH_TOKEN" # Authorization: Basic YWRtaW46YWRtaW4=

    printSectionLine  "Upload package $PACKAGE"

    PackageUpload $PACKAGE_FILE $PAKAGE_NAME $SOURCE_HOST $HEADER_AUTH_TOKEN $DOLOG

    printSectionLine  "Package uploaded $PACKAGE"
}







