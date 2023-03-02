1. 개요
Google Vison api의 Object detection 기능을 이용해 카메라에서 일정 주기마다 사진을 보내어 특정 객체를 감지할 때 마다 알림을 보내는 프로그램과 알림을 수신하는 앱을 만드는 것을 목표로 하였습니다. 카메라가 사람(객체)을 감지하면 DB서버에 해당 시간과 위치정보, 사진을 저장하고 안드로이드 앱으로 알림을 보냅니다. 앱에서는 카메라 감지 기록과 화면을 볼 수 있습니다.

2. 개발환경
App : Android App
사용 라이브러리/API : Volley(http 통신), GoogleMap(지도), Jsoup(html에서 
json파싱), Geocoder(GPS좌표를 주소로 변환), FCM(알림기능), google vision API

Server : Google Cloud Platform(Linux Ubuntu 18.04.2 LTS)
		: Apache 웹서버, PHP, MariaDB 사용
	Naver Cloud Platform(Linux Ubuntu 16.04)
		: tornado 웹서버, python 사용	

3. 주요 기능
로그인 기능 : 로그인시, DB에서 아이디와 비밀번호를 체크한 후에, 로그인ID를 앱에  저장하고, 알림 수신을 위한 Firebase 인스턴스 ID를 DB에 저장합니다.
자동 로그인 : 앱을 실행시, 저장된 로그인ID를 확인하고, DB에 Firebase 인스턴스 ID를 스마트폰 내의 ID와 비교하여 같을 시, 자동으로 로그인을 수행합니다.

기록 보기 :  앱에서 앱서버의DB를 조회하여 카메라에서 사람을 확인한 시간과 카메라의 위치를 지도로 확인할 수 있습니다.

카메라 송신 : 카메라가 있는 노트북이나 웹캠이 부착된 카메라에서 프로그램을 실행해, 카메라 서버로 jpg프레임을 보냅니다.

카메라 보기 : 수신받은 jpg프레임을 web(html img태그)으로 출력합니다. 앱에서 Webview를 통해 카메라화면을 볼 수 있습니다.

알림 받기 : 알림 설정을 하면 알림 정보를 DB에 저장한 후, DB에 저장된 알림 정보와 Firebase 인스턴스 ID를 이용해 알림을 보내면, 앱에서 수신합니다. 

객체(사람)　탐지 : 일정 주기마다 카메라의 한 프레임을 Google Vision API를 사용하여 사람(특정 객체)이 있는지 판단합니다.

![제목 없음](https://user-images.githubusercontent.com/44769598/222374361-dd56a409-eddc-4bb0-addf-88af478c0bd8.png)
