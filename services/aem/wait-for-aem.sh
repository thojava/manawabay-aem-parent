#!/bin/bash

# Copyright 2020 Adobe Systems Incorporated
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# DO NOT MODIFY
#

# wait-for-grid.sh
set -e

cmd="$@"

# set default to http://host.docker.internal:4502
AEM_URL=${AEM_URL:-http://host.docker.internal:4502}
AEM_AUTH=${AEM_AUTH:-admin:admin}
STARTUP_TIMEOUT=${STARTUP_TIMEOUT:-300}

echo 'Test Connect to AEM: ${AEM_URL}'
# curl to aem and do not fail if it is not available
curl -v -u ${AEM_AUTH} --header Referer:localhost --connect-timeout 1 --max-time 1 ${AEM_URL}/system/console/bundles.json || true

echo 'Waiting for the AEM'
echo -n "."
while ! (curl -u ${AEM_AUTH} --header Referer:localhost --silent --connect-timeout 1 --max-time 1 ${AEM_URL}/system/console/bundles.json \
        | grep -q \"state\":\"Installed\") && [[ "$SECONDS" -lt ${STARTUP_TIMEOUT} ]]; do
    echo -n "."
done

echo ''

if [[ "$SECONDS" -ge ${STARTUP_TIMEOUT} ]]; then
    >&2 echo "AEM failed to start in ${STARTUP_TIMEOUT} seconds"
    exit 1
fi

>&2 echo "AEM is up"

exec $cmd
