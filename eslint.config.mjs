import { FlatCompat } from '@eslint/eslintrc'
import mochaPlugin from 'eslint-plugin-mocha'
import globals from "globals";
const compat = new FlatCompat()
export default [
  {
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: "commonjs",
      globals:{
        ...globals.browser
      }
    },
  },
  mochaPlugin.configs.flat.recommended, {
    rules: {
      'mocha/no-exclusive-tests': 'error',
      'mocha/no-skipped-tests': 'error',
      'mocha/no-mocha-arrows': 'off'
    }
  },
  ...compat.config(
    {
      extends: ['plugin:cypress/recommended'],
      rules: {
        'cypress/no-unnecessary-waiting': 'off'
      }
    })
]
