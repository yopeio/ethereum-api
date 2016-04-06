contract owner {
    struct  Profile {
        string name;
        string surname;
    }

    function getName() returns (string _name) {
        return name;
    }

    function getSurname() returns (string _surname) {
        return surname;
    }

}

contract bicycle is owner {

    address public regOwner;

    function registerOwner(address owner) {
        regOwner = owner;
    }

    function getOwner() returns (address owner) {
        return regOwner;
    }
}