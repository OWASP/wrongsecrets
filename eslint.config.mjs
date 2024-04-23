import globals from "globals";
import pluginJs from "@eslint/js";
import cypress from "eslint-plugin-cypress";
import chai from "eslint-plugin-chai-friendly";

// convert the rest of eslintrc.js!
export default [
  {
    languageOptions: {
      globals:{
        ...globals.browser,
        cy: "readonly"
      }
    },
    plugins: {
      cypress:cypress,
      chai
    }
  },
  pluginJs.configs.recommended,
];
