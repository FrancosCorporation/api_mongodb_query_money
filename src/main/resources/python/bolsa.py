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
        opened = []
        high = []
        low = []
        close = []
        price = []
        volume = []
        date = []
        list = []
        for a in df:
            carteira.append(a+".SA")

        dt = (yfinance.download(carteira, start="2001-01-01", end="2022-07-20"))
        for f in range(len(carteira)):
            print(str(len(carteira))+"-of-"+str(f))
            # print(carteira[f])
            if(str(dt.index[i]) != None):
                for i in range(len(dt)):
                    date.append(str(dt.index[i]))
                    opened.append(dt["Open"].values[i])
                    high.append(dt["High"].values[i])
                    low.append(dt["Low"].values[i])
                    close.append(dt["Close"].values[i])
                    price.append(dt["Adj Close"].values[i])
                    volume.append(dt["Volume"].values[i])
            else:
                break
            list.append(date)
            list.append(opened)
            list.append(high)
            list.append(low)
            list.append(close)
            list.append(price)
            list.append(volume)
            carteira.append(list)
            first = len(carteira)-1
            second = len(carteira[len(carteira)-1])
            third = len(carteira[len(carteira)-1][second-1])
            print(str(first) + "  " + str(second) + "  " + str(third))
            # print(len(carteira[749][6]))
            # carteira[749] limit total acoes
            # carteira[749][5522] ocorrencias de (Open) abertura, (High) preco maximo , Low preco minimo, (Close) preco fechamento ,(Adj Close) price, (Volume) , Date (str(dt.index[i])

            with open('src/main/resources/python/dados.txt', 'w',  encoding='utf-8') as file:
                caract = 4
                for i in range(first):
                    # f.write(carteira[i]+"\n")
                    file.write(
                        "                                              "+carteira[i].replace(".SA", "")+"\n \n \n")
                    file.write(space(caract)+"Date "+space(caract)+" Open "+space(caract)+" High "+space(caract) +
                               " Low "+space(caract)+" Close "+space(caract)+" Price "+space(caract)+" Volume "+space(caract)+"\n")
                    # range of first array is
                    for e in range(third):
                        conct = ""
                        for j in range(second):
                            if(str(carteira[first][j][e]).__contains__(".") and len(str(carteira[first][j][e])) >= 10):
                                conct += str(carteira[first]
                                             [j][e])[:5]+(space(caract))
                            elif(len(str(carteira[first][j][e])) > 10):
                                conct += str(carteira[first]
                                             [j][e])[:10]+(space(caract))

                            else:
                                conct += (space(caract-2)) + \
                                    str(carteira[first][j][e])+(space(caract))
                        file.write(conct+"\n")
            file.close()
    except Exception as e:
        print(e)


mycode()
