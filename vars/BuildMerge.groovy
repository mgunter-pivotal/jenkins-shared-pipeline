import groovy.json.JsonSlurperClassic
def call(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  pipeline {
    agent any  
    options {
      buildDiscarder(logRotator(numToKeepStr:'5')) 
      skipStagesAfterUnstable()
      disableConcurrentBuilds()
    }
    stages {  
      stage ('Set Default values') { 
        steps {
            defaultProps(config)
        }
      }

      // stage ('Build'){
      //   when {
      //       expression {
      //           return env.BRANCH_NAME != 'master';
      //       }
      //   }
      //   steps {
      //     script {
      //         build(config)
      //     }
      //   }
      // }

      stage ('Merge branch to master request'){
          steps {
              mergeBranch(config)
          }
      }
    }

    post {
        success {
            echo 'The build job finised successfully'
        }
        failure {
            echo 'The build job failed '
        }
        unstable {
            echo 'The build job is  unstable'
        }
    }

  }
 
}




