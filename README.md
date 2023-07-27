# event-driven-microservices

Spring Boot와 Spring Cloud 스택을 사용하여 마이크로서비스를 개발하며, 이는 대부분의 의존성을 자동 설정을 통해 제공함으로써 개발 시간을 단축시킵니다. 각각의 서비스 개발 과정에서는 마이크로서비스 패턴을 적용합니다.

이 프로젝트에서 사용할 몇 가지 패턴은 외부 설정, API 버전 관리, 서비스 발견, API 게이트웨이, 서킷 브레이커, 요율 제한, 이벤트 소싱, CQRS, OAuth2와 OpenID Connect 프로토콜을 사용한 인증 및 인가, 모니터링, 분산 추적, 로그 집계 및 시각화 등입니다.

이 프로젝트에서는 Kafka를 이벤트 스토어로 사용하고, Elasticsearch를 쿼리 부분 및 로그 집계에 사용할 예정입니다. 또한 Docker를 사용하여 모든 것을 컨테이너화하여 향후 어떤 클라우드 벤더로든 쉽게 애플리케이션을 전달할 수 있도록 할 것입니다.

## Architecutre

<img width="1680" alt="Untitled (3)" src="https://github.com/Jimoou/event-driven-microservices/assets/109801772/83f48917-e702-4e39-82be-2be25e96f494">
