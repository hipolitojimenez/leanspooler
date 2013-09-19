Scenario: Add a new print job using curl.

Given the user execute the command ../../cmds/curl --header "printer: web" --data-binary @../../cmds/demoJob.pdf http://localhost:8080/leanspooler/add
When the command returns
Then the database contains the new print job


Scenario: Add a new print job using wget.

Given the user execute the command ../../cmds/wget -qO- --header="printer: web" --post-file ../../cmds/demoJob.pdf -q http://localhost:8080/leanspooler/add
When the command returns
Then the database contains the new print job
