node {

    properties([
		buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), 

		// Bellow line trigers this job every time
		pipelineTriggers([pollSCM('* * * * * ')])
		])


    stage("Pull Repo"){
		git 'https://github.com/farrukh90/cool_website.git'
}
	stage("Install Prerequiset"){
		sh """
		ssh centos@dev1.awsumar.com                    sudo yum install httpd -y
		"""
}
	stage("Copy Artifacts"){
		sh """
		scp -r * centos@dev1.awsumar.com:/tmp              
		ssh centos@dev1.awsumar.com                        sudo cp -r /tmp/index.html /var/www/html/
		ssh centos@dev1.awsumar.com                        sudo cp -r /tmp/style.css /var/www/html/
		ssh centos@dev1.awsumar.com                        sudo chown centos:centos /var/www/html/
		ssh centos@dev1.awsumar.com                         sudo chmod 777 /var/www/html/*

		"""
}
	stage("Restart web server"){
		sh "ssh centos@dev1.awsumar.com             sudo systemctl restart httpd"
}
	stage("Slack"){
		slackSend color: '#BADA55', message: 'Hello, World!'
	}
}
