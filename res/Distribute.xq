import module namespace d="http://basex.org/modules/distribute";
let $urls:=('mond02.inf.uni-konstanz.de:20000','mond03.inf.uni-konstanz.de:20000','mond04.inf.uni-konstanz.de:20000')
let $query:=("<result>{count(collection('socket_rrs_43')/descendant::title)}</result>")
let $results:=d:query($query, $urls)
let $info:=<infos>{for $a in d:info() return <info>{$a}</info>}</infos>
(: let $info:= <info>bla</info> :)
return <dq-results>{
        $info,
        for $r in $results
        return $r
       }</dq-results>

