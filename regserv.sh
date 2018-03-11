#!/bin/bash

script_dir=$(cd `dirname $0`; pwd)
service_name=$1
script_name=$2
runtype=$3
cat >>/lib/systemd/system/$service_name.service<<EOF
[Unit]
Description=DEMO
After=network.target auditd.service
ConditionPathExists=!${script_dir}/${service_name}_not_to_be_run

[Service]
EnvironmentFile=-${script_dir}/environmentfile
ExecStart=${script_dir}/${script_name}
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=${service_name}
KillMode=control-group
Restart=always
Type=${runtype}

[Install]
WantedBy=multi-user.target
Alias=${service_name}.service
EOF
systemctl enable $service_name
systemctl daemon-reload
service $service_name restart
