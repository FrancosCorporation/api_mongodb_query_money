import numpy as np
import sys
import investpy
import yfinance
import os
# os.system(f'powershell "pip install -r src/main/resources/python/requirements.txt"')
# import b3api

# data = b3api.assets.getAll()
# data())

# df = investpy.get_stocks(country='United States')


def space(char):
    return ("-"*char)


def mycode():
    try:
        # df = investpy.get_stocks(country='Brazil')[sys.argv[1]]
        df = investpy.get_stocks(country='Brazil')["symbol"]
        carteira = []
        for a in df:
            carteira.append(a+".SA")
        caract = ";"
        dt = (yfinance.download(carteira,
              start="2001-01-01", end="2022-07-20"))
        with open('C:/Users/Rodolfo/git/api_mongodb_query_money/src/main/resources/python/dados2Brutos.txt', 'w',  encoding='utf-8') as file:
            print(len(dt.index))
            for j in range(len(dt)):
                print(str(j)+"-of-"+str(len(carteira)))
                file.write("\n                                               " +
                      carteira[j].replace(".SA", "")+"\n")
                file.write("Date "+caract+" Open "+caract+" High "+caract +
                      " Low "+caract+" Close "+caract+" Price "+caract+" Volume "+"\n")
                for i in range(len(dt)):
                    file.write(str(dt.index[i])[:10]+caract+str(dt["Open"].values[i])[:6]+caract+str(dt["High"].values[i])[:6]+caract+str(dt["Low"].values[i])[:6]+caract+str(dt["Close"].values[i])[:6]+caract+str(dt["Adj Close"].values[i])[:6]+caract+str(dt["Volume"].values[i])+"\n")
        file.close()
    except Exception as e:
        print(e)


mycode()
