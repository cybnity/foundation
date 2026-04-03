#! /bin/bash
echo "-- PARALLEL PORTS FORWARDING --"

# start each service and show their PID for easy killing
sh access-control-sso-system-external-port-forwarding.sh &
AC_SYSTEM_PID=$!
echo "--- Access Control SSO system PID=$AC_SYSTEM_PID"

sh reactive-backend-system-external-port-forwarding.sh &
REACT_MSG_GW_PID=$!
echo "--- Reactive Backend system PID=$REACT_MSG_GW_PID"

sh web-reactive-frontend-system-external-port-forwarding.sh &
WEB_REACT_FRONTEND_PID=$!
echo "--- Web Reactive Frontend system PID=$WEB_REACT_FRONTEND_PID"

sh ui-apis-gateway-system-external-port-forwarding.sh &
UI_APIS_GW_PID=$!
echo "--- UI Apis Gateway system PID=$UI_APIS_GW_PID"

echo "-- All external ports are forwarded.\n ctrl-c to kill them in one time ;) --"
trap onexit INT
function onexit() {
    kill -9 $WEB_REACT_FRONTEND_PID
    kill -9 $REACT_MSG_GW_PID
    kill -9 $AC_SYSTEM_PID
    kill -9 $UI_APIS_GW_PID
}
