node {
	// Bellow lines sets "Discards builds more than 5 "
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
		ssh jenkins_worker1.awsumar.com   sudo yum install httpd -y
		"""
}
	stage("Copy Artifacts"){
		sh """
		scp -r * centos@jenkins_worker1.awsumar.com:/tmp
		ssh centos@jenkins_worker1.awsumar.com                        sudo cp -r /tmp/index.html /var/www/html/
		ssh centos@jenkins_worker1.awsumar.com                        sudo cp -r /tmp/style.css /var/www/html/
		ssh centos@jenkins_worker1.awsumar.com                        sudo chown centos:centos /var/www/html/
		ssh centos@jenkins_worker1.awsumar.com                         sudo chmod 777 /var/www/html/*

		"""
}
	stage("Restart web server"){
		sh "ssh centos@${ENVIR}               sudo systemctl restart httpd"
}
	stage("Slack"){
		slackSend color: '#BADA55', message: 'Hello, World!'
	}
}
