
Shallow Diff
----

A simplified diff/patch solution learning from immutable-diff/patch.

Values are compared with `identical?` for performace in special scenarios.

### Usage

```clojure
[shallow-diff "0.1.1"]
```

```clojure
(shallow-diff.diff/diff x1 x2)
(shallow-diff.patch/patch x1 operations)
```

Only `hash-map`, `hash-set`, `vector` are supported.
For other types of primitives, it generates `set!` to replace.

### Operations

Format `[coord [op arg1 args2]]`, examples:

* `[[1   ]  [:add     2  "x"]]`
* `[[:map]  [:remove  1     ]]`

For values:

* `[:set! a-val]`

For hash maps:

* `[:add  a-key a-val]`
* `[:drop a-key      ]`

For vectors:

* `[:insert a-key a-val]`
* `[:remove a-key      ]`
* `[:append a-val      ]`

For hash sets:

* `[:include a-val]`
* `[:exclude a-val]`

### Develop

Clojure code is generated from JSON with Cirru toolchains.

Start editor:

```bash
npm i -g cirru-light-editor
cle cirru/
# open browser http://repo.cirru.org/light-editor/
```

Run tests:

```bash
boot watch-test
```

Get Clojure code by compiling:

```bash
boot compile-cirru
```

### License

MIT
