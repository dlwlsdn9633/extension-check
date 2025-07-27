# 플로우 파일 확장자 차단 과제
Spring Boot 기반의 확장자 차단 시스템입니다.
업로드 보안 강화를 위해 고정 확장자와 커스텀 확장자를 구분하여 관리하며, 최대 200개의 커스텀 확장자를 등록할 수 있습니다.

## 사이트 주소
- http://49.50.135.52/

## 개발 환경
- **Frontend**: Thymeleaf
- **Backend**: Spring Boot
- **Persistence** MyBatis
- **Database**: H2 (In-Memory)

## ERD
<img width="830" height="142" alt="Image" src="https://github.com/user-attachments/assets/5d61becc-6821-4ec3-8768-9a9c36acdcdb" />

- **blocked_extensions**: 차단된 확장자 데이터를 저장하는 테이블입니다.
- **attache_files**: 저장된 파일들의 정보를 저장하는 테이블입니다.

## 기능 요약
### 1. 고정 확장자 (Fixed Extension) 관리

사용자가 고정 확장자 중 하나 체크 박스를 체크하면, ext와 customType(1)을 포함한 데이터가 Ajax를 통해 '/v1/api'에 POST요청으로 전송됩니다.

customType이 1인 경우, 서버는 이를 **고정 확장자**로 판단하고, handleFixedExtension() 메서드를 호출합니다.

#### 1) 확장자 차단 목록 확인
<img width="535" height="248" alt="Image" src="https://github.com/user-attachments/assets/4ed6dd87-9cac-47ca-b684-a0fd6c9ba3c7" />
<br />

handleFixedExtension() 내부에서 extensionService.isBlocked(ext)를 호출해, 해당 확장자가 현재 차단 목록에 존재하는지 확인합니다.

만약, **존재할 경우** 해당 레코드를 삭제하여 차단 해제를 합니다.

만약, **존재하지 않을 경우** 새 레코드를 추가하여 차단 등록을 진행합니다.

## 2. 커스텀 확장자 관리

사용자가 커스텀 확장자를 입력하고 추가 버튼을 클릭하면, ext와 customType(2)를 포함한 데이터를 AJAX를 통해 '/v1/api'에 POST 요청을 진행합니다.

customType이 2인 경우, 서버는 이를 **커스텀 확장자**로 간주하고, handleCustomExtension 메소드를 호출합니다.

handleCustomExtension에서는 다음과 같은 3단계 검증을 수행합니다.

<img width="538" height="274" alt="Image" src="https://github.com/user-attachments/assets/e26760c8-0e21-4b25-955c-140d5fdc7499" />

### 1) 최대 등록 개수 제한
고정 확장자를 제외한 모든 커스텀 확장자 목록을 가져옵니다.
만약, 해당 리스트의 크기가 200개를 초과할 경우, 등록을 거부합니다.

### 2) 고정 확장자 중복 방지
입력된 확장자가 FixedExtension Enum에 정의된 고정 확장자인지 isFixedExt(ext) 메소드를 통해 확인합니다.
고정 확장자일 경우, 커스텀으로 등록하지 않도록 차단합니다.

### 3) 커스텀 중복 방지
extensionService.isBlocked(ext) 메소드를 호출해, 이미 동일한 커스텀 확장자가 등록되어 있는지 확인합니다.
(예: 이미 sh가 등록되어 있는데 다시 sh를 등록하려는 경우 차단합니다)

위 모든 검증을 통과한 경우에만, 해당 커스텀 확장자를 차단 목록에 추가합니다.

## 3. 새로고침 시, 체크 및 저장한 값 유지
<img width="540" height="274" alt="Image" src="https://github.com/user-attachments/assets/e9f84de5-685a-461e-95a9-5c75405e28e6" />

페이지 새로고침 시, AdminController에서는 전체 blockedExtensionList 중

**FixedExtension Enum**에 해당하는 값만 필터링하여 fixedBlockedList를 생성하고, 나머지는 customBlockedList로 분리합니다.

이후 화면에서는 전체 고정 확장자 목록을 순회하며, fixedBlockedList에 포함된 확장자에는 체크가 유지되도록 하고, 그 외 커스텀 확장자들은 배지 형태로 출력됩니다.

## 4. 파일 업로드시

### 4.1 파일 첨부시

<img width="365" height="78" alt="Image" src="https://github.com/user-attachments/assets/344bc840-2f6e-454b-b221-f59f3b773537" />

파일 첨부 시, AJAX를 호출해 첨부된 파일의 ext와 customType(0)을 '/v1/api'에 POST 방식으로 요청을 보냅니다.
해당 요청은 서버의 handleTotalExtension 메소드로 전달됩니다.

handleTotalExtension은 전달받은 확장자(ext)가 blocked_extensions 테이블에 등록되어 있는지 확인합니다.
차단된 확장자일 경우, 첨부 자체를 차단하고, 파일 input값을 초기화해 업로드를 무효화합니다.

## 4.2 파일 업로드시

<img width="378" height="278" alt="Image" src="https://github.com/user-attachments/assets/485187d9-448e-40da-a37a-7727e51f4515" />

업로드가 진행되면, 실제 업로드는 FileService.uploadFiles 메서드에서 처리됩니다.
이 메서드 내에서 extensionService.isBlocked를 통해 한 번 더 확장자 차단 여부를 확인합니다.

만약, 차단된 확장자인 경우 업로드 실패 처리를 수행하고, 나머지 작업을 진행합니다.

## 5. 그 외 처리사항
### 5.1 Fixed Extension 확장성 고려

<img width="266" height="285" alt="Image" src="https://github.com/user-attachments/assets/0c32f5e0-6d1f-4789-b5a8-571297b31698" />

FixedExtension을 Enum으로 정의함으로써, 추후 고정 확장자를 추가할 때 해당 Enum에 Label과 함께 값만 추가하면, 자동으로 고정 확장자가 추가됩니다.

또한, Controller에서는 모든 고정 확장자의 value 값을 수집하여 View로 전달하고, Thymeleaf에서는 이 값을 순회하며 출력하므로, View 단에서 확장자 추가로 인한 View 작업을 수행할 필요가 없습니다.

### 5.2 유효성 검사

<img width="467" height="97" alt="Image" src="https://github.com/user-attachments/assets/576e279f-f1c0-440f-acac-0e8644d6275f" />

고정 확장자든 커스텀 확장자든, Controller에서 전달받은 ext는 처리에 앞서 먼저 해당 값이 존재하는지와 길이가 20자를 초과하지 않는지를 검사합니다.

### 5.3 커스텀 확장자 고정 확장자에 포함되는지 여부 체크
FixedExtension의 Enum에 해당 ext가 Fixed Extension(고정 확장자)인지 체크하는 메소드를 정의하였고, 해당 메소드를 통해 입력받은 ext가 고정 확장자인지 여부를 판단하도록 제작했습니다.

만약, 입력된 커스텀 확장자가 고정 확장자에 포함된다면, 해당 커스텀 확장자는 등록되지 않습니다.


### 5.3 확장자 중복 작업 방지 및 알림 처리 구현

<img width="629" height="266" alt="Image" src="https://github.com/user-attachments/assets/e4281788-2c60-4e42-af6b-14842cba928f" />

동일한 화면에서 여러 관리자가 동시에 작업할 수 있는 상황을 고려하여, 확장자 추가 또는 삭제 시 중복 작업을 감지해 알림창을 표시하고, 중복된 작업은 실패 처리되도록 구현하였습니다.
