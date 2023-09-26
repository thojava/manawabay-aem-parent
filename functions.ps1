
# Get-DateStamp
# Returns a date stamp in the format of yyyyMMdd-HHmmss
Function Get-DateStamp
{
    [Cmdletbinding()]
    [Alias("DateStamp")]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$Text
    )

    return "{0:yyyyMMdd}-{0:HHmmss}" -f (Get-Date)

}

# Test-Path
# Tests if a path exists on the AEM instance
Function TestPaths
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string[]]$PATH_BATCH,
        [string]$SOURCE_HOST_TEST,
        [bool]$DOLOG = $False
    )

    $RESULT = $True
    if ($DOLOG) {
        Write-Host "Check paths"
    }
    $PATH_BATCH | ForEach-Object {
        $TEST_PATH = $_
        $TEST_URL = "${SOURCE_HOST_TEST}$_.json"
        $TEST_RESULT = Invoke-Expression -Command "curl -s -o /dev/null -w ""%{http_code}"" ${TEST_URL}"
        if ($DOLOG) {
            Write-Host "${TEST_URL}: ${TEST_RESULT}"
        }
        # set $RESULT to false and if $RESULT is false, it will not be overwritten
        if ($TEST_RESULT -ne "200") {
            $RESULT = $False
        }
    }
    return $RESULT
}

# WaitForPaths
# Waits for a path to exist on the AEM instance
Function WaithForPaths
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string[]]$PATH_BATCH,
        [string]$SOURCE_HOST_TEST,
        [int]$WAIT_SECONDS = 10,
        [bool]$DOLOG = $False
    )

    $RESULT = $True
    if ($DOLOG) {
        Write-Host "Wait for paths ${WAIT_SECONDS} seconds"
    }
    $PATH_BATCH | ForEach-Object {
        $TEST_PATH = $_
        $TEST_URL = "${SOURCE_HOST_TEST}$_.json"
        if ($DOLOG) {
            Write-Host "Wait for $TEST_URL"
        }

        $WAIT_SECONDS_PATH = $WAIT_SECONDS
        # wait untill TEST_URL is return 200
        $TEST_RESULT = Invoke-Expression -Command "curl -s -o /dev/null -w ""%{http_code}"" ${TEST_URL}"
        while ($TEST_RESULT -ne "200") {
            if ($DOLOG) {
                Write-Host -NoNewline "."
            }
            Start-Sleep -s 1
            $TEST_RESULT = Invoke-Expression -Command "curl -s -o /dev/null -w ""%{http_code}"" ${TEST_URL}"
            if ($WAIT_SECONDS_PATH -gt 0) {
                $WAIT_SECONDS_PATH = $WAIT_SECONDS_PATH - 1
            } else {
                if ($DOLOG) {
                    Write-Host ""
                    Write-Host "Timeout"
                }
                $RESULT = $False
                break
            }
        }
        # set $RESULT to false and if $RESULT is false, it will not be overwritten
        if ($TEST_RESULT -ne "200") {
            $RESULT = $False
        }

    }


    return $RESULT
}

# GetStringHash
# Returns a hash of a string
Function GetStringHash
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$STRING
    )

    $stringAsStream = [System.IO.MemoryStream]::new()
    $writer = [System.IO.StreamWriter]::new($stringAsStream)
    $writer.write($STRING)
    $writer.Flush()
    $stringAsStream.Position = 0
    return (Get-FileHash -InputStream $stringAsStream | Select-Object Hash).Hash

}

# CreatePackageName
# Creates a package from a list of paths
Function CreatePackageName
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string[]]$PATH_BATCH,
        [string]$PREFIX = "dev"
    )

    $RETURN = ""
    $NAME_MAP = [System.Collections.ArrayList]::new()
    if ($PATH_BATCH.count -lt 5) {
        $PATH_BATCH | ForEach-Object {
            $PATH_NAMES = $_.Split("/")
            $NAME_MAP = $NAME_MAP + $PATH_NAMES
        }
    } else {
        # if more than 5 create a hash of the array $PATH_BATCH
        $NAME_MAP = "-" + (GetStringHash ($PATH_BATCH -join ""))
    }
    $NAME_MAP_STRING = ( $NAME_MAP | Select-Object -Unique ) -join "-"
    $RETURN = "${PREFIX}${NAME_MAP_STRING}"
    return $RETURN

}

# PackageCheck
# Checks if a package exists on the AEM instance
Function PackageCheck
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )

    $SERVICE_CHECK = "/crx/packmgr/list.jsp?_charset_=utf-8&includeVersions=true&path=${PACKAGE_PATH}"
    $COMMAND_PACKAGE_CHECK = "curl -s ${HEADER_AUTH_TOKEN} ""${SOURCE_HOST}${SERVICE_CHECK}"" "

    if ($DOLOG) {
        Write-Host "Command Check:`n ${COMMAND_PACKAGE_CHECK}"
    }

    $COMMAND_PACKAGE_CHECK_RESULT = Invoke-Expression $COMMAND_PACKAGE_CHECK

    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_CHECK_RESULT}"
    }

    $COMMAND_PACKAGE_CHECK_RESULT_JSON = $COMMAND_PACKAGE_CHECK_RESULT | ConvertFrom-Json
    $COMMAND_PACKAGE_CHECK_RESULT_TOTAL = $COMMAND_PACKAGE_CHECK_RESULT_JSON.total

    if ($DOLOG) {
        Write-Host "Check Result Total:`n ${COMMAND_PACKAGE_CHECK_RESULT_TOTAL}"
    }

    if ($COMMAND_PACKAGE_CHECK_RESULT_TOTAL -eq "0") {
        return $False
    } else {
        return $True
    }

}

# PackageCreate
# Creates a package on the AEM instance
Function PackageCreate
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )

    # create package
    $SERVICE_CREATE = "/crx/packmgr/service/exec.json?cmd=create"

    $COMMAND_PACKAGE_CREATE = "curl -s -X POST -F""_charset_=utf-8"" -F""packageName=${PAKAGE_NAME}"" -F""groupName=${PACKAGE_GROUP}"" ${HEADER_AUTH_TOKEN} ${SOURCE_HOST}${SERVICE_CREATE} "

    if ($DOLOG) {
        Write-Host "Command Create:`n ${COMMAND_PACKAGE_CREATE}"
    }

    $COMMAND_PACKAGE_CREATE_RESULT = Invoke-Expression $COMMAND_PACKAGE_CREATE

    if ($DOLOG) {
        Write-Host "Command Result:`n ${COMMAND_PACKAGE_CREATE_RESULT}"
    }

    $COMMAND_PACKAGE_CREATE_RESULT_JSON = $COMMAND_PACKAGE_CREATE_RESULT | ConvertFrom-Json
    $COMMAND_PACKAGE_CREATE_RESULT_SUCCESS = $COMMAND_PACKAGE_CREATE_RESULT_JSON.success

    if ($DOLOG) {
        Write-Host "Command Result Success:`n ${COMMAND_PACKAGE_CREATE_RESULT_SUCCESS}"
    }

    if ($COMMAND_PACKAGE_CREATE_RESULT_SUCCESS -eq $True) {
        return $True
    } else {
        return $False
    }
}

# PackageDelete
# Deletes a package on the AEM instance
Function PackageDelete
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )

    # delete package
    $SERVICE_DELETE = "/crx/packmgr/service/.json${PACKAGE_PATH}?cmd=delete"

    $COMMAND_PACKAGE_DELETE = "curl -s -X POST ${HEADER_AUTH_TOKEN} ${SOURCE_HOST}${SERVICE_DELETE} "

    if ($DOLOG) {
        Write-Host "Command Delete:`n ${COMMAND_PACKAGE_DELETE}"
    }

    $COMMAND_PACKAGE_DELETE_RESULT = Invoke-Expression $COMMAND_PACKAGE_DELETE


    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_DELETE_RESULT}"
    }


    $COMMAND_PACKAGE_DELETE_RESULT_JSON = $COMMAND_PACKAGE_DELETE_RESULT | ConvertFrom-Json
    $COMMAND_PACKAGE_DELETE_RESULT_SUCCESS = $COMMAND_PACKAGE_DELETE_RESULT_JSON.success

    if ($DOLOG) {
        Write-Host "Command Result Success:`n ${COMMAND_PACKAGE_DELETE_RESULT_SUCCESS}"
    }

    if ($COMMAND_PACKAGE_DELETE_RESULT_SUCCESS -eq $True) {
        return $True
    } else {
        return $False
    }
}

# PackageUpdate
# Updates a package on the AEM instance
Function PackageUpdate
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )


    # update package
    $SERVICE_UPDATE = "/crx/packmgr/update.jsp"

    # filter: [{"root":"/content/capital/datastore/accd","rules":[]}]
    $PACKAGE_FILTER = ""
    $PATH_BATCH | ForEach-Object {
        $PACKAGE_FILTER = "${PACKAGE_FILTER}{'root':'$_','rules':[]},"
    }
    $PACKAGE_FILTER = $PACKAGE_FILTER.TrimEnd(",")
    $PACKAGE_FILTER = "[${PACKAGE_FILTER}]"

    if ($PACKAGE_FILTER.Length -gt 300) {
        # write $PACKAGE_FILTER to temp file
        $PACKAGE_FILTER_FILE = ".${PACKAGE_PATH}.filter"
        $PACKAGE_FILTER | Out-File $PACKAGE_FILTER_FILE
        $PACKAGE_FILTER = "@.${PACKAGE_PATH}.filter"
    }

    $COMMAND_PACKAGE_UPDATE = "curl -s -X POST -F""_charset_=utf-8"" -F""path=${PACKAGE_PATH}"" -F""packageName=${PAKAGE_NAME}"" -F""groupName=${PACKAGE_GROUP}"" -Ffilter=""${PACKAGE_FILTER}"" ${HEADER_AUTH_TOKEN} ${SOURCE_HOST}${SERVICE_UPDATE} "

    if ($DOLOG) {
        Write-Host "Command Update:`n ${COMMAND_PACKAGE_UPDATE}"
    }

    $COMMAND_PACKAGE_UPDATE_RESULT = Invoke-Expression $COMMAND_PACKAGE_UPDATE


    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_UPDATE_RESULT}"
    }


    $COMMAND_PACKAGE_UPDATE_RESULT_JSON = $COMMAND_PACKAGE_UPDATE_RESULT | ConvertFrom-Json
    $COMMAND_PACKAGE_UPDATE_RESULT_SUCCESS = $COMMAND_PACKAGE_UPDATE_RESULT_JSON.success

    if ($DOLOG) {
        Write-Host "Command Result Success:`n ${COMMAND_PACKAGE_UPDATE_RESULT_SUCCESS}"
    }

    if ($COMMAND_PACKAGE_UPDATE_RESULT_SUCCESS -eq $True) {
        return $True
    } else {
        return $False
    }
}

# PackageBuild
# Builds a package on the AEM instance
Function PackageBuild
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )


    # build package
    # $SERVICE_BUILD = "/crx/packmgr/service/script.html/etc/packages/my_packages/${PAKAGE_NAME}.zip?cmd=build"
    $SERVICE_BUILD = "/crx/packmgr/service/.json${PACKAGE_PATH}?cmd=build"

    $COMMAND_PACKAGE_BUILD = "curl -s -X POST ${HEADER_AUTH_TOKEN} ${SOURCE_HOST}${SERVICE_BUILD} "



    if ($DOLOG) {
        Write-Host "Command Build:`n ${COMMAND_PACKAGE_BUILD}"
    }

    $COMMAND_PACKAGE_BUILD_RESULT = Invoke-Expression $COMMAND_PACKAGE_BUILD


    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_BUILD_RESULT}"
    }


    $COMMAND_PACKAGE_BUILD_RESULT_JSON = $COMMAND_PACKAGE_BUILD_RESULT | ConvertFrom-Json
    $COMMAND_PACKAGE_BUILD_RESULT_SUCCESS = $COMMAND_PACKAGE_BUILD_RESULT_JSON.success

    if ($DOLOG) {
        Write-Host "Command Result Success:`n ${COMMAND_PACKAGE_BUILD_RESULT_SUCCESS}"
    }

    if ($COMMAND_PACKAGE_BUILD_RESULT_SUCCESS -eq $True) {
        return $True
    } else {
        return $False
    }
}

# PackageReplicate
# Replicates a package on the AEM instance
Function PackageReplicate
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )


    # replicate package
    # $SERVICE_BUILD = "/crx/packmgr/service/script.html/etc/packages/my_packages/${PAKAGE_NAME}.zip?cmd=replicate"
    $SERVICE_REPLICATE = "/crx/packmgr/service/.json${PACKAGE_PATH}?cmd=replicate"

    $COMMAND_PACKAGE_REPLICATE = "curl -s -X POST ${HEADER_AUTH_TOKEN} ${SOURCE_HOST}${SERVICE_REPLICATE} "

    Write-Host "Command Replicate:`n ${COMMAND_PACKAGE_REPLICATE}"


    if ($DOLOG) {
        Write-Host "Command Replicate:`n ${COMMAND_PACKAGE_REPLICATE}"
    }

    $COMMAND_PACKAGE_REPLICATE_RESULT = Invoke-Expression $COMMAND_PACKAGE_REPLICATE


    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_REPLICATE_RESULT}"
    }


    $COMMAND_PACKAGE_REPLICATE_RESULT_JSON = $COMMAND_PACKAGE_REPLICATE_RESULT | ConvertFrom-Json
    $COMMAND_PACKAGE_REPLICATE_RESULT_SUCCESS = $COMMAND_PACKAGE_REPLICATE_RESULT_JSON.success

    if ($DOLOG) {
        Write-Host "Command Result Success:`n ${COMMAND_PACKAGE_REPLICATE_RESULT_SUCCESS}"
    }

    if ($COMMAND_PACKAGE_REPLICATE_RESULT_SUCCESS -eq $True) {
        return $True
    } else {
        return $False
    }
}

# PackageDownload
# Downloads a package from the AEM instance
Function PackageDownload
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$PACKAGE_DESTINATION_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )


    # download package
    # curl -u admin:admin http://localhost:4502/etc/packages/export/name_of_package.zip > name_of_package.zip

    $COMMAND_PACKAGE_DOWNLOAD = "curl -s ${HEADER_AUTH_TOKEN} --output ""${PACKAGE_DESTINATION_PATH}"" ${SOURCE_HOST}${PACKAGE_PATH}"

    if ($DOLOG) {
        Write-Host "Command Dowload:`n ${COMMAND_PACKAGE_DOWNLOAD}"
    }

    $COMMAND_PACKAGE_DOWNLOAD_RESULT = Invoke-Expression $COMMAND_PACKAGE_DOWNLOAD

    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_DOWNLOAD_RESULT}"
    }

    # if $FILE_PACKAGE exists return true
    if (Test-Path $PACKAGE_DESTINATION_PATH) {
        return $True
    } else {
        return $False
    }

}

# PackageUpload
# Uploads a package on the AEM instance
Function PackageUpload
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_FILE,
        [string]$PAKAGE_NAME,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )


    # upload package
    # curl -u admin:admin -F file=@"name of zip file" -F name="name of package" -F force=true -F install=true http://localhost:4502/crx/packmgr/service.jsp

    # if $FILE_PACKAGE exists return true
    if (Test-Path $PACKAGE_FILE) {
        # return $True
    } else {
        Write-Host "Package does not exist: ${PACKAGE_FILE}"
        return $False
    }

    $CURL_FLAGS = "-s "
    if ($DOLOG) {
        $CURL_FLAGS = " -v"
    }

    $COMMAND_PACKAGE_UPLOAD = "curl ${CURL_FLAGS} ${HEADER_AUTH_TOKEN} --insecure -F file=@""${PACKAGE_FILE}"" -F name=""${PAKAGE_NAME}"" -F force=true -F install=true ${SOURCE_HOST}/crx/packmgr/service.jsp"

    if ($DOLOG) {
        Write-Host "Command Upload:`n ${COMMAND_PACKAGE_UPLOAD}"
    }

    $COMMAND_PACKAGE_UPLOAD_RESULT = Invoke-Expression $COMMAND_PACKAGE_UPLOAD

    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_UPLOAD_RESULT}"
    }

}

# PackageInstall
# Installs a package on the AEM instance
Function PackageInstall
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_FILE,
        [string]$PAKAGE_NAME,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )


    $SERVICE_INSTALL = "/crx/packmgr/service/.json${PACKAGE_PATH}?cmd=install&extractOnly=false&autosave=1024&recursive=true&acHandling=&dependencyHandling=required"

    $COMMAND_PACKAGE_INSTALL = "curl -s -X POST ${HEADER_AUTH_TOKEN} ${SOURCE_HOST}${SERVICE_REPLICATE} "

    Write-Host "Command Replicate:`n ${COMMAND_PACKAGE_INSTALL}"

    if ($DOLOG) {
        Write-Host "Command Replicate:`n ${COMMAND_PACKAGE_INSTALL}"
    }

    $COMMAND_PACKAGE_INSTALL_RESULT = Invoke-Expression $COMMAND_PACKAGE_INSTALL


    if ($DOLOG) {
        Write-Host "Check Result:`n ${COMMAND_PACKAGE_INSTALL_RESULT}"
    }

    $COMMAND_PACKAGE_INSTALL_RESULT_JSON = $COMMAND_PACKAGE_INSTALL_RESULT | ConvertFrom-Json
    $COMMAND_PACKAGE_INSTALL_RESULT_SUCCESS = $COMMAND_PACKAGE_INSTALL_RESULT_JSON.success

    if ($DOLOG) {
        Write-Host "Command Result Success:`n ${COMMAND_PACKAGE_INSTALL_RESULT_SUCCESS}"
    }

    if ($COMMAND_PACKAGE_INSTALL_RESULT_SUCCESS -eq $True) {
        return $True
    } else {
        return $False
    }

}

# PackageGetFilters
# Gets the filters of a package on the AEM instance
Function PackageGetFilters
{
    [Cmdletbinding()]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$PACKAGE_PATH,
        [string]$SOURCE_HOST,
        [string]$HEADER_AUTH_TOKEN,
        [bool]$DOLOG = $False
    )

    $SERVICE_CHECK = "/crx/packmgr/list.jsp?_charset_=utf-8&includeVersions=true&path=${PACKAGE_PATH}"
    $COMMAND_PACKAGE_FILTER = "curl -s ${HEADER_AUTH_TOKEN} ""${SOURCE_HOST}${SERVICE_CHECK}"" "

    if ($DOLOG) {
        Write-Host "Command Filters:`n ${COMMAND_PACKAGE_FILTER}"
    }

    $COMMAND_PACKAGE_FILTER_RESULT = Invoke-Expression $COMMAND_PACKAGE_FILTER

    if ($DOLOG) {
        Write-Host "Filter Result:`n ${COMMAND_PACKAGE_FILTER_RESULT}"
    }

    $COMMAND_PACKAGE_FILTER_RESULT_JSON = $COMMAND_PACKAGE_FILTER_RESULT | ConvertFrom-Json -Depth 10
    $COMMAND_PACKAGE_FILTER_RESULT_TOTAL = $COMMAND_PACKAGE_FILTER_RESULT_JSON.total

    if ($DOLOG) {
        Write-Host "Filter Result Total:`n ${COMMAND_PACKAGE_FILTER_RESULT_TOTAL}"
    }

    if ($COMMAND_PACKAGE_FILTER_RESULT_TOTAL -ne "0") {
        $COMMAND_PACKAGE_FILTER_RESULT_JSON_RESULTS = $COMMAND_PACKAGE_FILTER_RESULT_JSON.results
        $COMMAND_PACKAGE_FILTER_RESULT_JSON_RESULTS_FILTER = $COMMAND_PACKAGE_FILTER_RESULT_JSON_RESULTS | Select-Object -ExpandProperty filter
        $COMMAND_PACKAGE_FILTER_RESULT_JSON_RESULTS_FILTER_PATH = [System.Collections.ArrayList]::new()
        $COMMAND_PACKAGE_FILTER_RESULT_JSON_RESULTS_FILTER | ForEach-Object {
            $PACKAGE_FILTER = $_ | Select-Object -ExpandProperty root
            [void]$COMMAND_PACKAGE_FILTER_RESULT_JSON_RESULTS_FILTER_PATH.add($PACKAGE_FILTER)
        }
        return $COMMAND_PACKAGE_FILTER_RESULT_JSON_RESULTS_FILTER_PATH
    } else {
        return [System.Collections.ArrayList]::new()
    }


}

[string]$FUNCTIONS_URI = "https://github.com/aem-design/aemdesign-docker/releases/latest/download/functions.ps1"

# set global variables
$SKIP_CONFIG = $true
$PARENT_PROJECT_PATH = "."
$LOG_PEFIX = "pm"
$DIR_NAME = Split-Path $PWD -Leaf
$DOCKER_NETWORK_NAME = "${DIR_NAME}"
$LOG_FILENAME = "$SOURCE_PATH_LIST$(DateStamp).log"
$LOG_RESULT_FILENAME = "$SOURCE_PATH_LIST$(DateStamp).result.log"
$LOG_CURL_FILENAME = "$SOURCE_PATH_LIST$(DateStamp).curl.log"
$LOG_PARALLEL_FILENAME = "./logs/$SOURCE_PATH_LIST$(DateStamp).parallel.log"

# include global helper functions
. ([Scriptblock]::Create((([System.Text.Encoding]::ASCII).getString((Invoke-WebRequest -Uri "${FUNCTIONS_URI}").Content))))
