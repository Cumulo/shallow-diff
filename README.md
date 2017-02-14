
Shallow Diff
----

A simplified diff/patch solution learning from immutable-diff/patch.

Values are compared with `identical?` for performace in special scenarios.
There's still much room for optimizations in vector diffing.

### Usage

```clojure
[cumulo/shallow-diff "0.1.1"]
```

```clojure
(shallow-diff.diff/diff x1 x2)
(shallow-diff.patch/patch x1 operations)
```

Only `hash-map`, `hash-set`, `vector` are supported.
For other types of primitives, it generates `set!` to replace.

### Operations

Format `[coord [op arg1 args2]]`, examples:

```clojure
[[1   ]  [:add     2  "x"]]

[[:map]  [:remove  1     ]]
```

For values:

```clojure
[:set! a-val]
```

For hash maps:

```clojure
[:add  a-key a-val]

[:drop a-key      ]
```

For vectors:

```clojure
[:insert a-key a-val]

[:remove a-key      ]

[:append a-val      ]
```

For hash sets:

```cljure
[:include a-val]

[:exclude a-val]
```

### Develop

https://github.com/mvc-works/stack-workflow

### License

MIT
