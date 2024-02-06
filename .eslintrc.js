module.exports = {
  env: {
    jest: true,
    'cypress/globals': true,
    browser: true,
    commonjs: true,
    es2021: true
  },
  extends: [
      'standard',
      'plugin:chai-friendly/recommended'
  ],
  overrides: [
  ],
  parserOptions: {
    ecmaVersion: 'latest'
  },
  rules: {
  },
  plugins: [
      'cypress',
      'chai-friendly'
  ]
};
