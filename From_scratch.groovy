node {
	properties([
		// Below line sets "Discard Builds more than 5"
		properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), pipelineTriggers([pollSCM('* * * * * ')])])
	
		// Pulls a repo from developer
	stage("Pull Repo"){
		git 'https://github.com/farrukh90/cool_website.git'
	}
		//Installs web server on different environment
	stage("Install Prerequisites"){
		sh """
		ssh centos@jenkins_master.awsumar.com                sudo yum install httpd -y
		"""
	}
		//Copies over developers files to different environment
	stage("Copy artifacts"){
		sh """
		scp -r *  centos@jenkins_master.awsumar.com:/tmp
		ssh centos@jenkins_master.awsumar.com                sudo cp -r /tmp/index.html /var/www/html/
		ssh centos@jenkins_master.awsumar.com                sudo cp -r /tmp/style.css /var/www/html/
		ssh centos@jenkins_master.awsumar.com			    sudo chown centos:centos /var/www/html/
		ssh centos@jenkins_master.awsumar.com			    sudo chmod 777 /var/www/html/*
		"""
	}
		//Restarts web server
	stage("Restart web server"){
		ws("tmp/") {
			sh "ssh centos@jenkins_master.awsumar.com              sudo systemctl restart httpd"
		}
	}

		//Sends a message to slack
	stage("Slack"){
		ws("mnt/"){
			slackSend color: '#BADA55', message: 'Hello, World!'
		}
	}
}