docker compose down
$CLEAN_CONTAINER = Read-Host 'Prune all containers? (docker system prune -f)'
if ($CLEAN_CONTAINER -eq 'y') {
    docker system prune -f
}
$CLEAN_VOLUMES = Read-Host 'Prune all volumes? (docker volume prune -f)'
if ($CLEAN_VOLUMES -eq 'y') {
    docker volume prune -f
}

$REMOVE_VOLUMES = Read-Host 'Remove container volumes? (docker volume prune -f)'
if ($CLEAN_VOLUMES -eq 'y')
{
    # get current director name
    $CURRENT_DIR = (Get-Item -Path ".\").Name

    docker volume rm "${CURRENT_DIR}_author-data"
    docker volume rm "${CURRENT_DIR}_publish-data"
}