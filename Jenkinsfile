import groovy.sql.Sql
pipeline {
    agent any
    parameters {
        string(name: 'table_name', defaultValue: 'customers', description: 'Name of the table to query')
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
        stage('Load database dump') {
            steps {
                script {
                    def classpath = "${JENKINS_HOME}/plugins/mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar"
                    this.class.classLoader.addURL(new URL("file:${classpath}"))
                    def mysql = com.mysql.jdbc.Driver
                    def connectionString = 'jdbc:mysql://158.160.25.14:3306/new_database'
                    def db = [
                        url: connectionString,
                        user: 'root',
                        password: 'P@ssw0rd',
                        driver: 'com.mysql.jdbc.Driver'
                    ]
                    def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
                    try {
                        def result = sql.firstRow("SHOW DATABASES LIKE 'new_database'")
                        if (!result) {
                        }
                    } catch (Exception ex) {
                        sh("""Hehe, try again!""")
                    }
                }
            }
        }
        stage('Take database dump') {
            steps {
                script {
                    def mysql = com.mysql.jdbc.Driver
                    def classpath = "${JENKINS_HOME}/plugins/mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar"
                    this.class.classLoader.addURL(new URL("file:${classpath}"))
                    def connectionString = 'jdbc:mysql://158.160.25.14:3306/new_database'
                    def db = [
                        url: connectionString,
                        user: 'root',
                        password: 'P@ssw0rd',
                        driver: 'com.mysql.jdbc.Driver'
                    ]
                    def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
                    try {
                        def dumpName = "db_dump_${params.BUILD_ID}.sql"
                        sql.execute("mysqldump --user=root --password=root_password new_database > ${dumpName}")
                        archiveArtifacts artifacts: dumpName
                    } catch (Exception ex) {
                        sh("""Hehe, try again!""")
                    }
                }
            }
        }
        stage('Execute SELECT query') {
            steps {
                script {
                    def classpath = "${JENKINS_HOME}/plugins/mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar"
                    this.class.classLoader.addURL(new URL("file:${classpath}"))
                    def mysql = com.mysql.jdbc.Driver
                    def connectionString = 'jdbc:mysql://158.160.25.14:3306/new_database'
                    def db = [
                        url: connectionString,
                        user: 'root',
                        password: 'P@ssw0rd',
                        driver: 'com.mysql.jdbc.Driver'
                    ]
                    def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
                    try {
                        def result = sql.firstRow("SELECT * FROM ${params.table_name}")
                    } catch (Exception ex) {
                        sh("""Hehe, try again!""")
                    }
                }
            }
        }
    }
}