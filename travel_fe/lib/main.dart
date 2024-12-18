import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Chat GPT Client',
      theme: ThemeData(
        primarySwatch: Colors.purple,
        fontFamily: 'Arial',
      ),
      home: ChatPage(),
    );
  }
}

class ChatPage extends StatefulWidget {
  @override
  _ChatPageState createState() => _ChatPageState();
}

class _ChatPageState extends State<ChatPage> {
  TextEditingController _controller = TextEditingController();
  String _response = "";

  Future<void> _sendPrompt() async {
    String prompt = _controller.text;

    if (prompt.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('프롬프트를 입력하세요'),
          backgroundColor: Colors.red,
        ),
      );
      return;
    }

    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/chat/recommend'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'input': prompt}),
      );

      if (response.statusCode == 200) {
        setState(() {
          _response = response.body;
        });
      } else {
        setState(() {
          _response = '오류: ${response.body}';
        });
      }
    } catch (e) {
      setState(() {
        _response = '오류: $e';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'P의 즉흥 여행',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        backgroundColor: Colors.deepPurple,
      ),
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            colors: [Colors.purple.shade100, Colors.purple.shade50],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            children: [
              TextField(
                controller: _controller,
                maxLines: null,
                style: TextStyle(fontSize: 18),
                decoration: InputDecoration(
                  hintText: '지금 기분을 말씀해 주세요.',
                  hintStyle: TextStyle(color: Colors.grey.shade600),
                  filled: true,
                  fillColor: Colors.white,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12.0),
                    borderSide: BorderSide.none,
                  ),
                  contentPadding: EdgeInsets.symmetric(
                    vertical: 14.0,
                    horizontal: 20.0,
                  ),
                  suffixIcon: Icon(Icons.mood, color: Colors.purple),
                ),
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: _sendPrompt,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepPurple, // 버튼 색상
                  padding: EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: Text(
                  '전송',
                  style: TextStyle(fontSize: 18,color: Colors.white, fontWeight: FontWeight.bold),
                ),
              ),
              SizedBox(height: 20),
              Expanded(
                child: Container(
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12.0),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.grey.withOpacity(0.5),
                        spreadRadius: 3,
                        blurRadius: 7,
                        offset: Offset(0, 3),
                      ),
                    ],
                  ),
                  padding: EdgeInsets.all(16.0),
                  child: SingleChildScrollView(
                    child: Text(
                      _response,
                      style: TextStyle(fontSize: 16, color: Colors.black87),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}