import datetime
import sys
import ephem


def main(argv):
    satellite = {
        "TianHe": ['1 48274U 21035A   21142.91230466  .00008662  00000-0  87040-4 0  9992',
                   '2 48274  41.4713  77.9043 0017825 345.1434  99.6260 15.65422358  3730',
                   '天和号：中国空间站;'],
        "ISS": ['1 25544U 98067A   21134.62864022  .00001854  00000-0  41806-4 0  9992',
                '2 25544  51.6435 148.7095 0003174   1.2012 151.3207 15.49005051283395',
                '国际空间站;'],
        "BILI": ['1 46454U 20065A   21135.09881137  .00001497  00000-0  96473-4 0  9999',
                 '2 46454  97.4994 192.0266 0016044 163.9710 281.4141 15.09733193 36512',
                 '吉林1号 高分03（Bilibili视频卫星）;'],
        "HABO": ['1 20580U 90037B   21134.59933003  .00000550  00000-0  22322-4 0  9992',
                 '2 20580  28.4702 289.0826 0002838  59.7162  16.2199 15.09637244505937',
                 '哈勃空间望远镜;'],
        "DongFangHong": ['1 04382U 70034A   21134.28873332 -.00000484  00000-0 -55194-4 0  9994',
                         '2 04382  68.4237 234.0056 1053813 206.7577 147.5205 13.08083176401954',
                         '东方红一号;'],
        "RUBBISH": ['1 47389C 21005AS  21135.14984602  .00050655  00000-0  35494-2 0  1355',
                    '2 47389  53.0186  19.8813 0001552  78.1416  71.7600 15.04682275    18',
                    'SpaceX 星链2110;']
    }
    line_1 = satellite.get(argv[1])[0]
    line_2 = satellite.get(argv[1])[1]
    sat: ephem.EarthSatellite = ephem.readtle('TianHe', line_1, line_2)
    sat.compute(datetime.datetime.utcnow())
    print(satellite.get(argv[1])[2])

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
