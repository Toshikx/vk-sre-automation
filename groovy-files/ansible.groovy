import groovy.sql.Sql
pipeline {
    agent any
    parameters {
        string(name: 'DB_HOST', description: 'Database host', defaultValue: '158.160.25.14')
        string(name: 'DB_PORT', description: 'Database port', defaultValue: '3306')
        string(name: 'DB_NAME', description: 'Database name', defaultValue: 'export')
        string(name: 'DB_USER', description: 'Database user', defaultValue: 'root')
        string(name: 'DB_PASSWORD', description: 'Database password', defaultValue: 'P@ssw0rd')
        string(name: 'TABLE_NAME', description: 'Table name for SELECT query')
    }
    stages {
        stage('Checkut') { // добавим новый Stage
            steps {
                git branch: 'main', url: "https://github.com/Toshikx/vk-sre-automation" // используем встроенный в Jenkins плагин Git для скачивания проекта из бранча main
            }
        }
        stage('Deploy') {
            steps {
                git branch: 'main', url: "https://github.com/Toshikx/vk-sre-automation"
                sh("""pwd""")
                sh("""ls -al""")
                sh("""ls -al ./role""")
                sh("""echo $PATH""")
                dir('role') {
                    ansiColor('xterm') {
                        ansiblePlaybook(
                            colorized: true,
                            disableHostKeyChecking: true,
                            credentialsId: "ssh-to-node",
                            installation: 'ansible',
                            sudoUser: 'tony',
                            inventory: 'hosts.yml',
                            playbook: 'lampstack.yml',
                            extras: "--timeout=60"
                        )
                    }
                }
            }
        }
        stage('Check Database') {
            steps {
                script {
                    def databaseExists = sh(script: "mysql -h ${params.DB_HOST} -P ${params.DB_PORT} -u ${params.DB_USER} -p${params.DB_PASSWORD} -e 'USE ${params.DB_NAME}' 2>&1 | grep -c 'Unknown database'", returnStatus: true) == 0 ? false : true
                    if (databaseExists) {
                        echo "Database exists"
                    } else {
                        error "Database does not exist"
                    }
                }
            }
        }
    }
}