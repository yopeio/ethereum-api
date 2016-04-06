contract test {
    function baz(uint32 x, bool y) returns (bool r) {
        r = x > 32 || y;
    }
}