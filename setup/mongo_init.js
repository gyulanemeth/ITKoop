db.objects.drop();
db.createCollection("objects");
db.objects.insert({ x : '50' , y : '50' , z : '0' , data : 'ITK' });
db.objects.insert({ x : '100', y : '100' , z : '0', data : 'Kooperatív' });
db.objects.insert({ x : '200', y : '200' , z : '0', data : 'Izé' });

