
Shallow Diff
----

A simplified diff/patch solution learning from immutable-diff/patch.

### Usage

> not yet...

### Operations

Format `[coord [operation arg1 args2]]`, examples:

* `[[1   ]  [:add  2  "x"]]`
* `[[:map]  [:rm   :b    ]]`

For values:

* `[:set! a-val]`

For hash maps:

* `[:add  a-key a-val]`
* `[:drop a-key      ]`

For vectors:

* `[:insert a-key a-val]`
* `[:remove a-key      ]`

For hash sets:

* `[:include a-val]`
* `[:exclude a-val]`

### License

MIT
