## PURPOSE
Tools and commands allowing to manage the Node.js web application supported by ReactJS technology as web frontend module.

# NODEJS USAGE

## NODEJS VERSION MANAGEMENT
<details><summary>Prepare development stack</summary>
<p>

From project directory, update Node version to the latest version

```shell
#From Mac
node -v
  
# Install Node version manager
# - N manager installed via NPM
npm install -g n
# or installed via brew
brew install n

# Install N packages manager of Node via
sudo n latest

# check node version updated
node -v
# check npm version updated
npm -v
```

</p>
</details>

## NPM RUN
<details><summary>Reinstall project modules</summary>
<p>

Remove all node_modules sub-folders, the package-lock.json file, and clear the npm cache

```shell
rm -rf node_modules
rm -f package-lock.json
npm cache clean --force
```

Execute from the web project folder:

```shell
npm install
```

</p>
</details>
<details><summary>Run application</summary>
<p>

Runs the app in the development mode.
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes.
You may also see any lint errors in the console.

```shell
npm start
```

</p>
</details>
<details><summary>Run tests</summary>
<p>

Launches the test runner in the interactive watch mode.
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

```shell
npm test
```

</p>
</details>
<details><summary>Build application</summary>
<p>

Builds the app for production to the `build` folder.
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

```shell
npm run build
```

[https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify](https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify)

</p>
</details>
<details><summary>Remove dependencies from project</summary>
<p>

**Note: this is a one-way operation. Once you `eject`, you can't go back!**

If you aren't satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you're on your own.

You don't have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn't feel obligated to use this feature. However we understand that this tool wouldn't be useful if you couldn't customize it when you are ready for it.

```shell
npm run eject
```

</p>
</details>

## REACTJS
<details><summary>Create React application</summary>
<p>
You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

</p>
</details>
<details><summary>Maintain ReactJS script on latest version</summary>
<p>   
From project folder, update all dependencies required by the reactjs application with react-script

```shell
npm install react-scripts@latest
```

</p>
</details>
<details><summary>Code Splitting</summary>
<p>

This section has moved here: [https://facebook.github.io/create-react-app/docs/code-splitting](https://facebook.github.io/create-react-app/docs/code-splitting)

</p>
</details>
<details><summary>Analyzing the Bundle Size</summary>
<p>

This section has moved here: [https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size](https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size)

</p>
</details>
<details><summary>Making a Progressive Web App</summary>
<p>

This section has moved here: [https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app](https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app)

</p>
</details>
<details><summary>Advanced Configuration</summary>
<p>

This section has moved here: [https://facebook.github.io/create-react-app/docs/advanced-configuration](https://facebook.github.io/create-react-app/docs/advanced-configuration)

</p>
</details>
<details><summary>Deployment</summary>
<p>

This section has moved here: [https://facebook.github.io/create-react-app/docs/deployment](https://facebook.github.io/create-react-app/docs/deployment)

</p>
</details>
