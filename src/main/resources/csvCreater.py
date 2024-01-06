import csv
from random import randint, uniform
from datetime import datetime, timedelta

# Fonction pour générer une date aléatoire dans l'intervalle donné
def random_date(start_date, end_date):
    delta = end_date - start_date
    random_days = randint(0, delta.days)
    return start_date + timedelta(days=random_days)

# Définition des paramètres
start_date = datetime(2023, 1, 1)
end_date = datetime(2024, 12, 31)
transaction_types = ['A', 'B', 'C', 'D']

# Génération des données
data = []
for i in range(1, 101):
    transaction_id = i
    account_number = randint(100000, 999999)
    transaction_date = random_date(start_date, end_date).strftime('%Y-%m-%d')
    transaction_type = transaction_types[randint(0, 3)]
    transaction_amount = round(uniform(10.0, 500.0), 2)

    data.append([transaction_id, account_number, transaction_date, transaction_type, transaction_amount])

# Écriture des données dans un fichier CSV
with open('data.csv', mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['transaction_id', 'account_number', 'transaction_date', 'transaction_type', 'transaction_amount'])
    writer.writerows(data)

print("Fichier CSV généré avec succès : data.csv")