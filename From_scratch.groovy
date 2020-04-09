node {
	properties([
		// Below line sets "Discard Builds more than 5"
		properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5'))
		// Below line triggers this job every minute
		pipelineTriggers([pollSCM('* * * * *')])
		parameters([
			// Asks for Environment to Build
			choice(choices: [
			'dev1.awsumar.com', 
			'qa1.awsumar.com', 
			'stage1.awsumar.com', 
			'prod1.awsumar.com'], 
			description: 'Please choose an environment', 
			name: 'ENVIR'),
         
		 ])

		// Pulls a repo from developer
	stage("Pull Repo"){
		 git 'https://github.com/farrukh90/cool_website.git'
	}
		//Installs web server on different environment
	stage("Install Prerequisites"){
		sh """
		ssh centos@${ENVIR}                 sudo yum install httpd -y
		"""
	}
		//Copies over developers files to different environment
	stage("Copy artifacts"){
		sh """
		scp -r *  centos@${ENVIR}:/tmp
		ssh centos@${ENVIR}                 sudo cp -r /tmp/index.html /var/www/html/
		ssh centos@${ENVIR}                 sudo cp -r /tmp/style.css /var/www/html/
		ssh centos@${ENVIR}				    sudo chown centos:centos /var/www/html/
		ssh centos@${ENVIR}				    sudo chmod 777 /var/www/html/*
		"""
	}
		//Restarts web server
	stage("Restart web server"){
		ws("tmp/") {
			sh "ssh centos@${ENVIR}               sudo systemctl restart httpd"
		}
	}

		//Sends a message to slack
	stage("Slack"){
		ws("mnt/"){
			slackSend color: '#BADA55', message: 'Hello, World!'
		}
	}
}