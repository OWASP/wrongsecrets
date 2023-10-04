module.exports = {
  env: {
    jest: true,
    'cypress/globals': true,
    browser: true,
    commonjs: true,
    es2021: true
  },
  extends: 'standard',
  overrides: [
  ],
  parserOptions: {
    ecmaVersion: 'latest'
  },
  rules: {
  },
  plugins: ['cypress']
}
