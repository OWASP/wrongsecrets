import { defineConfig } from "eslint/config";
import js from "@eslint/js";
import { FlatCompat } from '@eslint/eslintrc';
import mochaPlugin from 'eslint-plugin-mocha';
import cypressPlugin from 'eslint-plugin-cypress';
import globals from 'globals';
import babelParser from "@babel/eslint-parser";

const compat = new FlatCompat();


export default [
  {
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: 'module',
      globals: {
        ...globals.browser,
      },
      parser: babelParser,
      parserOptions: {
        requireConfigFile: false,
        babelOptions: {
          presets: ['@babel/preset-env'],
        },
      },
    },
  },
  {
    plugins: {
      mocha: mochaPlugin,
    },
    rules: {
      'mocha/no-exclusive-tests': 'error',
      'mocha/no-pending-tests': 'error',
      'mocha/no-mocha-arrows': 'off',
    },
  },
  {
    plugins: {
      cypress: cypressPlugin,
    },
    rules: {
      'cypress/no-unnecessary-waiting': 'off',
    },
    languageOptions: {
      globals: {
        ...cypressPlugin.configs.globals?.globals || {},
      },
    },
  },
];
