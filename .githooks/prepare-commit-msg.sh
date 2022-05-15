#!/bin/bash
project_prefix="SWOZO"
branchname=$(git branch | grep ^* | sed 's/\* //g' | sed 's/feature\///g')
if [ "$branchname" = "main" ] || [ "$branchname" = "develop" ]
    then
    exit
fi
  
originalmessage=`cat $1`

branch_number=$(echo $branchname | cut -d '-' -f 2)
commit_prefix=$project_prefix-$branch_number
  
if [[ $originalmessage == $commit_prefix* ]] || [[ $originalmessage == "[$commit_prefix]"* ]] || [[ $originalmessage == "[$project_prefix"* ]] || [[ $originalmessage == "$project_prefix"* ]] || [[ $originalmessage == "[NT]"* ]]; then
    exit
fi
echo "[$commit_prefix] $originalmessage" > $1
