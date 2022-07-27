import os
os.system(f'powershell "pip install -r src/main/resources/python/requirements.txt"')
import sys
import investpy
import yfinance
# import b3api

# data = b3api.assets.getAll()
# data())

# df = investpy.get_stocks(country='United States')


def space(char):
    return ("-"*char)


def downData(list):
    return yfinance.download(list, start="2001-01-01", end="2022-07-20")


def mycode():
    try:
        # df = investpy.get_stocks(country='Brazil')[sys.argv[1]]
        indexs = investpy.get_stocks(country='united States')["symbol"]
        carteira = []
        listofdata = []
        caract = ";"

        for a in indexs:
            carteira.append(a)
        for i in range(len(carteira)):
            listofdata.append(downData(carteira[i]))
        with open('src/main/resources/python/dados2Brutos.csv', 'w',  encoding='utf-8') as file:
            for j in range(len(listofdata)):
                print("------  "+str(j)+" -- of -- "+str(len(listofdata))+" ------")
                file.write(carteira[j].replace(".SA", ";")+"\n")
                file.write("Date"+caract+"Open"+caract+"High"+caract +
                           "Low"+caract+"Close"+caract+"Price"+caract+"Volume"+caract+"\n")
                for i in range(len(listofdata[j].index)):
                    file.write(str(listofdata[j].index[i])[:10]+caract+str(listofdata[j]["Open"].values[i])[:6]+caract+str(listofdata[j]["High"].values[i])[:6]+caract+str(listofdata[j]["Low"].values[i])[
                               :6]+caract+str(listofdata[j]["Close"].values[i])[:6]+caract+str(listofdata[j]["Adj Close"].values[i])[:6]+caract+str(listofdata[j]["Volume"].values[i])+caract+"\n")

        file.close()
    except Exception as e:
        print(e)


mycode()
