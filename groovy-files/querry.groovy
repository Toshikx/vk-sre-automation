import groovy.sql.Sql
pipeline {
    agent any
    parameters {
        string(name: 'DB_HOST', description: 'Database host', defaultValue: '158.160.25.14')
        string(name: 'DB_PORT', description: 'Database port', defaultValue: '3306')
        string(name: 'DB_NAME', description: 'Database name', defaultValue: 'classicmodels')
        string(name: 'DB_USER', description: 'Database user', defaultValue: 'root')
        string(name: 'DB_PASSWORD', description: 'Database password', defaultValue: 'P@ssw0rd')
        string(name: 'TABLE_NAME', description: 'Table name for SELECT query', defaultValue: 'customers')
    }
    stages{
        stage('Run Query') {
            when {
                expression { params.TABLE_NAME }
            }
            steps {
                script {
                    def result = sh(script: "mysql -h ${params.DB_HOST} -P ${params.DB_PORT} -u ${params.DB_USER} -p${params.DB_PASSWORD} -e 'SELECT * FROM ${params.TABLE_NAME}' ${params.DB_NAME}", returnStdout: true).trim()
                    echo "Query result: ${result}"
                }
            }
        }
    }
}