import module namespace d="http://basex.org/modules/distribute";
let $urls:=('mond02.inf.uni-konstanz.de:20000','mond03.inf.uni-konstanz.de:20000', 'mond04.inf.uni-konstanz.de:20000')
let $query:=("<result>{collection('socket_rrs_43')/descendant::title}</result>")
let $results:=d:query($query, $urls)
(: let $info:=d:info($query) :)
let $info:= <info>bla</info>
return <xml>{
        $info,
        for $r in $results
        return $r
       }</xml>

