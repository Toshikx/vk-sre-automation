import groovy.sql.Sql
pipeline {
    agent any
    parameters {
        string(name: 'DB_HOST', description: 'Database host', defaultValue: '158.160.25.14')
        string(name: 'DB_PORT', description: 'Database port', defaultValue: '3306')
        string(name: 'DB_NAME', description: 'Database name', defaultValue: 'classicmodels')
        string(name: 'DB_USER', description: 'Database user', defaultValue: 'root')
        string(name: 'DB_PASSWORD', description: 'Database password', defaultValue: 'P@ssw0rd')
    }
    stages {
        stage('Dump Database') {
            steps {
                sh "mysqldump -h ${params.DB_HOST} -P ${params.DB_PORT} -u ${params.DB_USER} -p${params.DB_PASSWORD} ${params.DB_NAME} > ${env.WORKSPACE}/db_dump.sql"
                archiveArtifacts artifacts: 'db_dump.sql', onlyIfSuccessful: true
            }
        }
        
        stage('Load Database') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    def databaseExists = sh(script: "mysql -h ${params.DB_HOST} -P ${params.DB_PORT} -u ${params.DB_USER} -p${params.DB_PASSWORD} -e 'USE ${params.DB_NAME};' 2>&1 | grep -c 'Unknown database'", returnStatus: true) == 1 ? false : true
                    if (!databaseExists) {
                        sh "mysql -h ${params.DB_HOST} -P ${params.DB_PORT} -u ${params.DB_USER} -p${params.DB_PASSWORD} -e 'CREATE DATABASE IF NOT EXISTS ${params.DB_NAME}'"
                    }
                    sh "mysql -h ${params.DB_HOST} -P ${params.DB_PORT} -u ${params.DB_USER} -p${params.DB_PASSWORD} ${params.DB_NAME} < ${env.WORKSPACE}/db_dump.sql"
                }
            }
        }
    }
}