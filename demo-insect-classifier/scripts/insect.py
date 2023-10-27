"""
PYTHON SCRIPT TO GIVE SAMPLE LABELS OF SCIENTIFIC NAMES OF INSECTS -> COMMON NAMES
WILL ALSO ADD INFORMATION ABOUT IF INSECT IS VENEMOUS
ALL EXPORTED TO CSV TITLED 'insect_labels.csv' TO BE PARSED BY _DROPDOWN.java

"""

import csv

common_csv = "common_names.csv"
sci_label_csv = "aiy_insects_V1_labelmap.csv"

hash = {}
lst_common_csv = []
lst_sci_label = []
with open(common_csv, newline = '') as file:
    reader = csv.reader(file)
    for row in reader:
        temp = row[1].split(' (')
        lst_common_csv.append(temp[0])
        hash[temp[0]] = row[0]

# print(lst_common_csv[:10])


with open(sci_label_csv, newline = '') as file:
    reader = csv.reader(file)
    h1 = next(reader)
    h2 = next(reader)
    for row in reader:
        lst_sci_label.append(row[1])

# print(lst_sci_label[:10])

set1 = set(lst_common_csv)
set2 = set(lst_sci_label)
common = set1.intersection(set2)
# print(common)
print(len(common))

# for sci in common:
#     print(sci + ": " + hash[sci])


with open("insect_labels.csv", mode = 'w', newline='') as file:
    writer = csv.writer(file)

    for i in range(len(lst_sci_label)):
        value = hash.get(lst_sci_label[i], "COMMON NAME")
        writer.writerow((lst_sci_label[i], value))
    
