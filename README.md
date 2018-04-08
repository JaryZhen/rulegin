
### Rule Engine Demo说明
- Step 1： Rule 定义
    
		{
		  "name": "in-press-alarm",
		  "weight": 0,
		  "dataSource": {
		    "type": "kafka",
			"configuration": {
				"topic": "Suct_Data",
				"format": "null",
		        "keys": [
		         "Suct_Pres_Status"
		        ]
		    }
		  },
		  "filters": [
		    {
		      "name": "吸气压力状态",
		      "type": "kafkaWindow",
			  "configuration": {
			  "size":2000,
			  "step":1000
			  },
		      "condition": "Sum(Suct_Pres_Status) > 70"
		    }
		  ],
		  "actions": [
		    {
		      "type": "Print",
		      "template": "吸气温度高"
		    }
		  ]
		}
- Step 2： Rule注册

		curl -X POST -H 'Content-Type: application/json' -d @Rule.json @localhost:8080/api/rule
- Step 3： Rule 启动

		首先Kafka 发送数据到Topic Suct_Data
		curl -X POST @localhost:8080/api/rule/ID/activate
- Step 4.   结果查验

		通过控制台来查看运行结果