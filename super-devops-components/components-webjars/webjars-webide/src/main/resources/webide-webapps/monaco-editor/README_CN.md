## This is a module of secondary encapsulation and integration of Microsoft Monaco editor. Its main goal is to realize more intelligent online programming prompt (based on user-defined language and class / attributes, etc.)

### Monaco documentations refer:
- [monaco-editor-sources](https://github.com/microsoft/monaco-editor)
- [monaco-custom-languages](https://microsoft.github.io/monaco-editor/playground.html#extending-language-services-custom-languages)
- [monaco-custom-languages-codingCompletion](https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.completionitemprovider.html#providecompletionitems)

- Code for smart completion(Core parts):

```
// Register a completion item provider for the new language
monaco.languages.registerCompletionItemProvider('mySpecialLanguage', {
    provideCompletionItems: (model, position, context, token) => {
        //
        // ***** Gets current line coding input chars. *****
        // @see https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.completionitemprovider.html#providecompletionitems
        //
        console.log("Current line coding input chars: " +
            model.getLineContent(position.lineNumber))

        var suggestions = [{
            label: 'simpleText',
            kind: monaco.languages.CompletionItemKind.Text,
            insertText: 'simpleText'
        }, {
            label: 'testing',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'testing(${1:condition})',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
        }, {
            label: 'ifelse',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: [
                'if (${1:condition}) {',
                '\t$0',
                '} else {',
                '\t',
                '}'
            ].join('\n'),
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'If-Else Statement'
        }];
        return { suggestions: suggestions };
    }
});
```

### Sample demonstration:
- [monaco-editor-plugin-codingCompletion-example](http://127.0.0.1:14070/webjars-example/webide/plugin/coding-completion/java)

