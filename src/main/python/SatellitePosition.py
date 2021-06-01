import datetime
import sys
import ephem


def main(argv):
    argument: str = ''
    for s in argv:
        argument += s
    argv: list = argument.split('@')
    name = argv[1]
    line_1 = argv[2]
    line_2 = argv[3]
    sat: ephem.EarthSatellite = ephem.readtle('Satellite', line_1, line_2)
    sat.compute(datetime.datetime.utcnow())
    print(name, ";")

    print("当前时间:", datetime.datetime.now(), ";")
    latitudeSplit = str(sat.sublat).split(":")
    latitude = latitudeSplit[0] + "°" + latitudeSplit[1] + "'" + latitudeSplit[2] + "''"
    if latitude.__contains__("-"):
        latitude = latitude.replace("-", "")
        latitude = "星下点纬度: " + latitude + " S"
        latitudeReverse = -1
    else:
        latitudeReverse = 1
        latitude = "星下点纬度: " + latitude + " N"
    print(latitude, ";")
    latitudeNum: float = float(latitudeSplit[0]) + \
                         latitudeReverse * float(latitudeSplit[1]) / 60 + \
                         float(latitudeSplit[2]) / 3600 * latitudeReverse

    longitudeSplit = str(sat.sublong).split(":")
    longitude = longitudeSplit[0] + "°" + longitudeSplit[1] + "'" + longitudeSplit[2] + "''"
    if longitude.__contains__("-"):
        longitudeReverse = -1
        longitude = longitude.replace("-", "")
        longitude = "星下点经度: " + longitude + " E"
    else:
        longitudeReverse = 1
        longitude = "星下点经度: " + longitude + " W"
    print(longitude, ";")
    longitudeNum: float = float(longitudeSplit[0]) + \
                          longitudeReverse * float(longitudeSplit[1]) / 60 + \
                          longitudeReverse * float(longitudeSplit[2]) / 3600

    print("海拔高度:", sat.elevation / 1000, "km")


if __name__ == '__main__':
    main(sys.argv)
