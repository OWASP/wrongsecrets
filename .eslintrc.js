module.exports = {
  env: {
    jest: true,
    'cypress/globals': true,
    browser: true,
    commonjs: true,
    es2021: true
  },
  extends: [
    'plugin:chai-friendly/recommended'
  ],
  overrides: [
  ],
  parserOptions: {
    ecmaVersion: 'latest'
  },
  plugins: [
    'cypress',
    'chai-friendly'
  ]
}
