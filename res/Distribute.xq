import module namespace d="http://basex.org/modules/distribute";
let $urls:=('mond02.inf.uni-konstanz.de:20000','mond03.inf.uni-konstanz.de:20000')
let $query:=("<result/>")
let $results:=d:query($query, $urls)
for $r in $results
return <one>{$r}</one>
