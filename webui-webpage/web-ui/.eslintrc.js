module.exports = {
    root: true,
    parser: '@babel/eslint-parser',
    settings: {
        react: {
            version: 'detect',
        },
    },
    plugins: ['react'],
    parserOptions: {
        requireConfigFile: false,
        ecmaFeatures: {
            experimentalObjectRestSpread: true,
            jsx: true,
        },
        plugins: [],
        presets: ['@babel/preset-env', '@babel/preset-react'],
    },
    extends: ['plugin:prettier/recommended', 'eslint:recommended'],
    rules: {
        'prettier/prettier': [
            'error',
            {
                endOfLine: 'auto',
            },
        ],
        'react/prop-types': 'off',
        'react/jsx-uses-react': 2,
        'react/jsx-uses-vars': 2,
        'react/react-in-jsx-scope': 2,
        'no-unused-vars': 'off',
        'no-case-declarations': 'off',
        'no-undef': 'off',
        'no-prototype-builtins': 'off',
        semi: [2, 'always'],
    },
    overrides: [
        {
            files: ['**/*.ts', '**/*.tsx'],
            parserOptions: {
                tsconfigRootDir: __dirname,
                project: './tsconfig.json',
                ecmaVersion: 2018,
                sourceType: 'module',
                // https://github.com/typescript-eslint/typescript-eslint/issues/6934
                warnOnUnsupportedTypeScriptVersion: false,
            },
            parser: '@typescript-eslint/parser',
            plugins: ['@typescript-eslint'],
            extends: [
                'eslint:recommended',
                'plugin:@typescript-eslint/recommended',
                'plugin:react/recommended',
                'plugin:react-hooks/recommended',
                'plugin:prettier/recommended',
            ],
            rules: {
                '@typescript-eslint/explicit-module-boundary-types': 'off',
                '@typescript-eslint/no-inferrable-types': 'off',
                '@typescript-eslint/no-explicit-any': 'off',
                '@typescript-eslint/ban-ts-comment': 'off',
                '@typescript-eslint/no-empty-function': 'off',
                '@typescript-eslint/no-namespace': 'off',
                'no-inner-declarations': 'off',
                'no-case-declarations': 'off',
                '@typescript-eslint/no-non-null-assertion': 'off',
                '@typescript-eslint/no-unused-vars': 'off',
                semi: [2, 'always'],
                'prettier/prettier': ['error', { endOfLine: 'auto' }],
                '@typescript-eslint/ban-types': [
                    'error',
                    {
                        types: {
                            '{}': false,
                        },
                        extendDefaults: true,
                    },
                ],
            },
        },
    ],
};
