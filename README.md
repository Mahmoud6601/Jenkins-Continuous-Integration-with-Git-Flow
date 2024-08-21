# Jenkins-Continuous-Integration-with-Git-Flow

This project uses Jenkins to automatically build and test code changes from this GitHub repository using a Jenkinsfile. The Git Flow branching model is applied to manage the development process.

## Tools Used

- Jenkins
- GitHub
- Git 

## Process

### Preparing the GitHub Repository

1. A new GitHub repository was created for this project.
2. The repository was initialized with a README.md, and contains two main branches:
    - `develop` for ongoing development.
    - `master` (or `main`) for stable releases.
3. The Git Flow model was applied to the repository:
    - Git Flow tools were installed.
    - Git Flow was initialized in the repository with default branch names.
4. A Jenkinsfile was added to the repository in the root directory.

### Configuring Jenkins

1. Necessary plugins, such as Git and Pipeline, were installed in Jenkins.
2. Jenkins was configured to connect to GitHub:
    - The "GitHub Integration Plugin" was installed via "Manage Jenkins" > "Manage Plugins" > "Available".
    - Credentials were set up in Jenkins for GitHub (username and token).
3. A new pipeline job was created:
    - A new item was selected, named "GitHub Pipeline", and "Pipeline" was chosen as the type.
    - In the pipeline configuration, "Pipeline script from SCM" was selected and "Git" was chosen as the SCM.
    - The repository URL and credentials were entered.
    - The branch to build was specified (e.g., */develop).

### Implementing Webhooks for Continuous Integration

1. In GitHub, the repository settings were accessed and "Webhooks" was selected.
2. A new webhook was added:
    - Payload URL: http://<<your-jenkins-url>>/github-webhook/ (remove <> and replace with your-jenkins-url)
    - Content type: application/json
    - "Just the push event" was selected.
    - The webhook was set to active.
3. With the webhook, Jenkins triggers a new build every time changes are pushed to the connected branch.

### Testing and Validation

1. A change was pushed to the develop branch of the GitHub repository.
2. Jenkins automatically triggered a build and processed according to the steps defined in the Jenkinsfile.
3. The output in Jenkins was checked to ensure the build and test stages were executed successfully.

After the completion of the project, the `develop` branch was merged with the `main` branch.
