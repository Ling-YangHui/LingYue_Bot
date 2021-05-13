import sys

from bce.option import Option
from bce.public.api import balance_chemical_equation
from bce.public.database import BUNDLED_ABBREVIATION_DATABASE
from bce.public.exception import ParserErrorWrapper, LogicErrorWrapper, InvalidCharacterException
from bce.public.option import MoleculeParserOptionWrapper


def main(argv):
    argv: list[str]
    string = ''
    for i in range(1, argv.__len__()):
        string += argv[i]
    opt = Option()
    MoleculeParserOptionWrapper(opt).set_abbreviation_mapping(BUNDLED_ABBREVIATION_DATABASE)
    try:
        result = balance_chemical_equation(string, opt)
        print(result)
    except ParserErrorWrapper:
        print("别发奇奇怪怪的东西给我!")
    except LogicErrorWrapper:
        print("逻辑，错误了呢")
    except InvalidCharacterException:
        print("方程式不太完整呢")


if __name__ == '__main__':
    main(sys.argv)
